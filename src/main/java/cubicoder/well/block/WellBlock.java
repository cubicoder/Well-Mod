package cubicoder.well.block;

import java.util.Random;

import cubicoder.well.block.entity.WellBlockEntity;
import cubicoder.well.config.WellConfig;
import cubicoder.well.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class WellBlock extends BaseEntityBlock {

	public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
	public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
	public static final BooleanProperty UPSIDE_DOWN = BooleanProperty.create("upside_down");
	
	public static final VoxelShape SHAPE_BASE = Shapes.join(Shapes.block(), Block.box(3.0D, 2.0D, 3.0D, 13.0D, 16.0D, 13.0D), BooleanOp.ONLY_FIRST);
	public static final VoxelShape SHAPE_INNER_SUPPORT = Shapes.or(
			Block.box(7.5D, 0.0D, 1.0D, 8.5D, 15.0D, 2.0D),
			Block.box(7.5D, 0.0D, 14.0D, 8.5D, 15.0D, 15.0D),
			Block.box(7.5D, 7.0D, 2.0D, 8.5D, 8.0D, 14.0D),
			Block.box(5.0D, 4.5D, 4.5D, 11.0D, 10.5D, 11.5D)
	);
	public static final VoxelShape SHAPE_ROOF = Shapes.or(
			Block.box(5.5D, 12.5D, 0.0D, 10.5D, 15.707D, 16.0D),
			Block.box(2.75D, 10.5D, 0.0D, 5.5D, 13.75D, 16.0D),
			Block.box(10.5D, 10.5D, 0.0D, 13.25D, 13.75D, 16.0D),
			Block.box(0.0D, 8.0D, 0.0D, 2.75D, 11.25D, 16.0D),
			Block.box(13.25D, 8.0D, 0.0D, 16.0D, 11.25D, 16.0D),
			SHAPE_INNER_SUPPORT
	);
	
	public WellBlock(MaterialColor mapColor) {
		this(Material.STONE, mapColor);
	}
	
	public WellBlock(Material material, MaterialColor mapColor) {
		this(Properties.of(material).color(mapColor).strength(3.0F, 1.5F).requiresCorrectToolForDrops());
	}
	
	public WellBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any()
				.setValue(AXIS, Direction.Axis.X)
				.setValue(HALF, DoubleBlockHalf.LOWER)
				.setValue(UPSIDE_DOWN, false));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return state.getValue(HALF) == DoubleBlockHalf.LOWER ? new WellBlockEntity(pos, state) : null;
	}
	
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return level.isClientSide ? null : createTickerHelper(type, ModBlocks.WELL_BE.get(), WellBlockEntity::serverTick);
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(AXIS, HALF, UPSIDE_DOWN);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		Direction.Axis axis = context.getPlayer().isCrouching() ? context.getHorizontalDirection().getClockWise().getAxis()
				: context.getHorizontalDirection().getAxis();
		
		// regular placement
		if (pos.getY() < level.getMaxBuildHeight() - 1) {
			if (level.getBlockState(pos.above()).canBeReplaced(context)) {
				return this.defaultBlockState().setValue(AXIS, axis).setValue(UPSIDE_DOWN, false);
			}
		}
		
		// upside down placement
		if (pos.getY() > level.getMinBuildHeight() + 1) {
			if (level.getBlockState(pos.below()).canBeReplaced(context)) {
				return this.defaultBlockState().setValue(AXIS, axis).setValue(UPSIDE_DOWN, true);
			}
		}
		
		return null;
	}
	
	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
		DoubleBlockHalf half = state.getValue(HALF);
		Direction flippedDir1 = state.getValue(UPSIDE_DOWN) ? Direction.DOWN : Direction.UP;
		Direction flippedDir2 = state.getValue(UPSIDE_DOWN) ? Direction.UP : Direction.DOWN;
		if (direction.getAxis() != Direction.Axis.Y || ((half == DoubleBlockHalf.LOWER) != (direction == flippedDir1)) || (neighborState.is(this) && (neighborState.getValue(HALF) != half))) {
			if ((half != DoubleBlockHalf.LOWER) || (direction != flippedDir2) || state.canSurvive(level, currentPos)) {
				return state;
			}
		}
		return Blocks.AIR.defaultBlockState();
	}
	
	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		level.setBlockAndUpdate(pos.above(state.getValue(UPSIDE_DOWN) ? -1 : 1), state.setValue(HALF, DoubleBlockHalf.UPPER));
		
		// warn placer if only one well can function in the area
		if (WellConfig.onlyOnePerChunk.get() && placer instanceof ServerPlayer) {
			BlockEntity be = level.getBlockEntity(pos);
			if (be instanceof WellBlockEntity && ((WellBlockEntity) be).nearbyWells > 1) {
				String message = state.getValue(UPSIDE_DOWN) ? "warn.well.onePerChunkFlipped" : "warn.well.onePerChunk";
				((ServerPlayer) placer).displayClientMessage(new TranslatableComponent(message), true);
			}
				
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
			return super.canSurvive(state, level, pos);
		} else {
			BlockState base = level.getBlockState(pos.below(state.getValue(UPSIDE_DOWN) ? -1 : 1));
			if (state.getBlock() != this) return super.canSurvive(state, level, pos);
			return base.is(this) && base.getValue(HALF) == DoubleBlockHalf.LOWER;
		}
	}
	
	@Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		if (!level.isClientSide) {
			if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
				BlockPos otherPos = pos.below(state.getValue(UPSIDE_DOWN) ? -1 : 1);
				BlockState otherState = level.getBlockState(otherPos);
				if (otherState.is(this) && otherState.getValue(HALF) == DoubleBlockHalf.LOWER) {
					if (player.isCreative()) {
						level.destroyBlock(otherPos, false, player);
					} else {
						level.destroyBlock(otherPos, canHarvestBlock(state, level, pos, player), player);
					}
				}
			}
		}

		super.playerWillDestroy(level, pos, state, player);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			BlockEntity be = level.getBlockEntity(pos);
			if (be instanceof WellBlockEntity) {
				((WellBlockEntity) be).countNearbyWells(e -> e.nearbyWells--);
			}
			super.onRemove(state, level, pos, newState, isMoving);
		}
	}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
			return InteractionResult.PASS;
		}
		
		if (!player.getItemInHand(hand).getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()) {
			return InteractionResult.PASS;
		}
		
		if (level.isClientSide) {
			return InteractionResult.SUCCESS;
		}
		
		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof WellBlockEntity) {
			WellBlockEntity well = (WellBlockEntity) be;
			
			boolean delayFlag = true;
			boolean fillingItem = FluidUtil.tryFillContainer(player.getItemInHand(hand), well.getTank(), Integer.MAX_VALUE, player, false).success;
			
			// only delay if drawing from the well with a fluid item
			if (fillingItem) {
				if (well.delayUntilNextBucket > 0) delayFlag = false;
			}
			
			if (delayFlag && FluidUtil.interactWithFluidHandler(player, hand, level, pos, hit.getDirection())) {
				if (WellConfig.playSound.get() && fillingItem) {
					level.playSound(null, pos.above(), ModSounds.CRANK.get(), SoundSource.BLOCKS, 0.25F, 1);
					well.delayUntilNextBucket = 32;
				}
				return InteractionResult.SUCCESS;
			}
		}
		
		return InteractionResult.PASS;
	}
	
	@Override
	public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
		if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
			BlockEntity be = level.getBlockEntity(pos);
			if (be instanceof WellBlockEntity) {
				FluidStack fluid = ((WellBlockEntity) be).getTank().getFluid();
				if (fluid != null && !fluid.isEmpty()) {
					int baseFluidLight = fluid.getFluid().getAttributes().getLuminosity();
					if (baseFluidLight > 0) {
						return Mth.clamp((int) (baseFluidLight * fluid.getAmount() / WellConfig.tankCapacity.get() + 0.5), 1, 15);
					}
				}
			}
		}
		
		return 0;
	}
	
	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
			BlockEntity be = level.getBlockEntity(pos);
			if (be instanceof WellBlockEntity) {
				WellBlockEntity well = (WellBlockEntity) be;
				FluidStack fluid = well.getTank().getFluid();
				
				if (fluid != null) {
					int amount = well.getTank().getFluidAmount();
					int capacity = well.getTank().getCapacity();
					boolean upsideDown = state.getValue(UPSIDE_DOWN);
					FluidState fluidState = fluid.getFluid().getAttributes().getStateForPlacement(level, pos, fluid);
					Material fluidMaterial = fluid.getFluid().getAttributes().getBlock(level, pos, fluidState).getMaterial();

					if (!upsideDown) {
						if (entity.getY() < (double) pos.getY() + getFluidRenderHeight(amount, capacity, upsideDown)) {
							// hardcoded behavior for lava and water based on cauldron
							if (fluidMaterial == Material.LAVA) {
								entity.lavaHurt();
							}
							
							if (fluidMaterial == Material.WATER) {
								if (!level.isClientSide && entity.isOnFire()) {
									entity.clearFire();
								}
							}
						}
					} else {
						if (entity.getY() > (double) pos.getY() + getFluidRenderHeight(amount, capacity, upsideDown)) {
							// no behavior for upside down fluids (yet?)
						}
					}
				}
			}
		}
	}
	
	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
		if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
			BlockEntity be = level.getBlockEntity(pos);
			if (be instanceof WellBlockEntity) {
				WellBlockEntity well = (WellBlockEntity) be;
				FluidStack fluid = well.getTank().getFluid();
				
				if (fluid != null) {
					int amount = well.getTank().getFluidAmount();
					int capacity = well.getTank().getCapacity();
					boolean upsideDown = state.getValue(UPSIDE_DOWN);
					float height = getFluidRenderHeight(amount, capacity, upsideDown);
					FluidState fluidState = fluid.getFluid().defaultFluidState();
					fluidState.animateTick(level, pos, random);

					// get around lava particle check
					if (fluid.getFluid() == Fluids.LAVA) {
						if (random.nextInt(100) == 0) {
							double x = (double) pos.getX() + random.nextDouble();
							double y = (double) pos.getY() + height;
							double z = (double) pos.getZ() + random.nextDouble();
							level.addParticle(ParticleTypes.LAVA, x, y, z, 0.0D, 0.0D, 0.0D);
							level.playLocalSound(x, y, z, SoundEvents.LAVA_POP, SoundSource.BLOCKS,
									0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
						}

						if (random.nextInt(200) == 0) {
							double x = (double) pos.getX() + 0.5;
							double y = (double) pos.getY() + height / 2;
							double z = (double) pos.getZ() + 0.5;
							level.playLocalSound(x, y, z, SoundEvents.LAVA_AMBIENT, SoundSource.BLOCKS,
									0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
						}
					}
				}
			}
		}
	}
	
	@Override
	public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, Entity entity) {
		// improve the roof sound if possible (some mods change the sound type of bricks to be better)
		return state.getValue(HALF) == DoubleBlockHalf.UPPER ? this.soundType : Blocks.BRICKS.getSoundType(state, level, pos, entity);
	}
		
	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.BLOCK;
	}
	
	@Override
	public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
		return false;
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
			return state.getValue(UPSIDE_DOWN) ? flipShapeUpsideDown(SHAPE_BASE) : SHAPE_BASE;
		} else if (state.getValue(AXIS) == Direction.Axis.X) {
			return state.getValue(UPSIDE_DOWN) ? flipShapeUpsideDown(SHAPE_ROOF) : SHAPE_ROOF;
		} else return state.getValue(UPSIDE_DOWN) ? flipShapeUpsideDown(flipShapeXZ(SHAPE_ROOF)) : flipShapeXZ(SHAPE_ROOF);
	}
	
	@Override
	public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
		if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
			return state.getValue(UPSIDE_DOWN) ? flipShapeUpsideDown(SHAPE_BASE) : SHAPE_BASE;
		} else if (state.getValue(AXIS) == Direction.Axis.X) {
			return state.getValue(UPSIDE_DOWN) ? flipShapeUpsideDown(SHAPE_INNER_SUPPORT) : SHAPE_INNER_SUPPORT;
		} else return state.getValue(UPSIDE_DOWN) ? flipShapeUpsideDown(flipShapeXZ(SHAPE_INNER_SUPPORT)) : flipShapeXZ(SHAPE_INNER_SUPPORT);
	}
	
	@Override
	public boolean useShapeForLightOcclusion(BlockState state) {
		return true;
	}
	
	@Override
	public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
		if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
			return Shapes.block();
		} else if (state.getValue(AXIS) == Direction.Axis.X) {
			return state.getValue(UPSIDE_DOWN) ? flipShapeUpsideDown(SHAPE_ROOF) : SHAPE_ROOF;
		} else return state.getValue(UPSIDE_DOWN) ? flipShapeUpsideDown(flipShapeXZ(SHAPE_ROOF)) : flipShapeXZ(SHAPE_ROOF);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		switch (rotation) {
		case COUNTERCLOCKWISE_90:
		case CLOCKWISE_90:
			switch ((Direction.Axis) state.getValue(AXIS)) {
			case Z:
				return state.setValue(AXIS, Direction.Axis.X);
			case X:
				return state.setValue(AXIS, Direction.Axis.Z);
			default:
				return state;
			}
		default:
			return state;
		}
	}
	
	/**
	 * Flips the VoxelShape from the X axis to the Z axis, or vice versa.
	 * @param shape the shape to be flipped
	 * @return the flipped VoxelShape
	 */
	public static VoxelShape flipShapeXZ(VoxelShape shape) {
		VoxelShape[] buffer = new VoxelShape[] { shape, Shapes.empty() };
		buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
			buffer[1] = Shapes.or(buffer[1], Shapes.create(minZ, minY, minX, maxZ, maxY, maxX));
		});
		
		return buffer[1];
	}
	
	/**
	 * Flips the VoxelShape upside down.
	 * @param shape the shape to be flipped
	 * @return the flipped VoxelShape
	 */
	public static VoxelShape flipShapeUpsideDown(VoxelShape shape) {
		VoxelShape[] buffer = new VoxelShape[] { shape, Shapes.empty() };
		buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
			buffer[1] = Shapes.or(buffer[1], Shapes.create(minX, 1 - maxY, minZ, maxX, 1 - minY, maxZ));
		});
		
		return buffer[1];
	}
	
	public static float getFluidRenderHeight(int amount, int capacity, boolean upsideDown) {
		float height = amount * 14F / (16 * capacity) + (2F / 16);
		return upsideDown ? 1 - height : height;
	}

}
