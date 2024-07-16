package cubicoder.well.block;

import com.mojang.serialization.MapCodec;
import cubicoder.well.block.entity.WellBlockEntity;
import cubicoder.well.config.WellConfig;
import cubicoder.well.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.world.AuxiliaryLightManager;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;

import javax.annotation.Nullable;

public class WellBlock extends Block implements EntityBlock {
	
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
	
	private DyeColor color;
	
	public WellBlock(DyeColor color) {
		super(BlockBehaviour.Properties.of()
				.mapColor(color)
				.instrument(NoteBlockInstrument.BASEDRUM)
				.strength(1.5F, 6.0F)
				.requiresCorrectToolForDrops());
		this.registerDefaultState(this.getStateDefinition().any()
				.setValue(AXIS, Direction.Axis.X)
				.setValue(HALF, DoubleBlockHalf.LOWER)
				.setValue(UPSIDE_DOWN, false));
		this.color = color;
	}
	
	public DyeColor getColor() {
		return color;
	}
	
	@Override
	protected MapCodec<? extends Block> codec() {
		return ModBlocks.WELL_CODEC.value();
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return state.getValue(HALF) == DoubleBlockHalf.LOWER ? new WellBlockEntity(pos, state) : null;
	}
	
	// from BaseEntityBlock
	@Nullable
	@SuppressWarnings("unchecked")
	private <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> serverType, BlockEntityType<E> clientType, BlockEntityTicker<? super E> ticker) {
		return serverType == clientType ? (BlockEntityTicker<A>) ticker : null;
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
		Direction.Axis axis = context.getHorizontalDirection().getAxis();
		if (context.getPlayer() != null && context.getPlayer().isCrouching()) {
			axis = context.getHorizontalDirection().getClockWise().getAxis();
		}
		
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
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		level.setBlockAndUpdate(pos.above(state.getValue(UPSIDE_DOWN) ? -1 : 1), state.setValue(HALF, DoubleBlockHalf.UPPER));
		
		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof WellBlockEntity well) {
			if (!level.isClientSide) {
				well.initFillTick();
				well.countNearbyWells(w -> {
					w.nearbyWells++;
					well.nearbyWells++;
				});
			}
			
			// warn placer if only one well can function in the area
			if (WellConfig.onlyOnePerChunk.get() && placer instanceof ServerPlayer) {
				if (well.nearbyWells > 1) {
					String message = state.getValue(UPSIDE_DOWN) ? "warn.well.onePerChunkFlipped" : "warn.well.onePerChunk";
					((ServerPlayer) placer).displayClientMessage(Component.translatable(message), true);
				}
			}
		}
	}
	
	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		if (state.getBlock() != this || state.getValue(HALF) == DoubleBlockHalf.LOWER) {
			return super.canSurvive(state, level, pos);
		} else {
			BlockState base = level.getBlockState(pos.below(state.getValue(UPSIDE_DOWN) ? -1 : 1));
			return base.is(this) && base.getValue(HALF) == DoubleBlockHalf.LOWER;
		}
	}
	
	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
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
		
		return super.playerWillDestroy(level, pos, state, player);
	}
	
	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			BlockEntity be = level.getBlockEntity(pos);
			if (be instanceof WellBlockEntity well) {
				well.countNearbyWells(w -> w.nearbyWells--);
			}
			super.onRemove(state, level, pos, newState, isMoving);
		}
	}
	
	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}
		
		if (player.getItemInHand(hand).getCapability(Capabilities.FluidHandler.ITEM) == null) {
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}
		
		if (!level.isClientSide) {
			BlockEntity be = level.getBlockEntity(pos);
			if (be instanceof WellBlockEntity well) {
				boolean delayFlag = true;
				boolean fillingItem = FluidUtil.tryFillContainer(player.getItemInHand(hand), well.getTank(), Integer.MAX_VALUE, player, false).success;
				
				// only delay if drawing from the well with a fluid item
				if (fillingItem) {
					if (well.delayUntilNextBucket > 0) delayFlag = false;
				}
				
				if (delayFlag && FluidUtil.interactWithFluidHandler(player, hand, level, pos, hitResult.getDirection())) {
					if (WellConfig.playSound.get() && fillingItem) {
						level.playSound(null, pos.above(), ModSounds.CRANK.get(), SoundSource.BLOCKS, 0.25F, 1);
						well.delayUntilNextBucket = 32;
					}
					return ItemInteractionResult.SUCCESS;
				}
			}
		}
		
		return ItemInteractionResult.sidedSuccess(level.isClientSide);
	}
	
	@Override
	public boolean hasDynamicLightEmission(BlockState state) {
		return state.getValue(HALF) == DoubleBlockHalf.LOWER;
	}
	
	@Override
	public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
		AuxiliaryLightManager lightManager = level.getAuxLightManager(pos);
		if (lightManager != null) {
			return lightManager.getLightAt(pos);
		} else {
			return 0;
		}
	}
	
	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
			BlockEntity be = level.getBlockEntity(pos);
			if (be instanceof WellBlockEntity well) {
				FluidStack fluid = well.getTank().getFluid();
				
				if (!fluid.isEmpty()) {
					if (isInFluid(entity.getY(), pos.getY(), well)) {
						FluidType fluidType = fluid.getFluid().getFluidType();
						
						// hardcoded behavior for lava based on cauldron
						if (fluidType == NeoForgeMod.LAVA_TYPE) {
							entity.lavaHurt();
						}
						
						// extinguish fire if possible
						if (fluidType.canExtinguish(entity)) {
							if (entity.isOnFire()) {
								entity.extinguishFire();
							}
						}
					}
				}
			}
		}
	}
	
	private boolean isInFluid(double entityY, int blockY, WellBlockEntity well) {
		double fluidHeight = getFluidHeight(well.getTank().getFluidAmount(), well.getTank().getCapacity(), well.isUpsideDown());
		return well.isUpsideDown() ? entityY > blockY + fluidHeight : entityY < blockY + fluidHeight;
	}
	
	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
			BlockEntity be = level.getBlockEntity(pos);
			if (be instanceof WellBlockEntity well) {
				FluidStack fluid = well.getTank().getFluid();
				
				if (!fluid.isEmpty()) {
					int amount = well.getTank().getFluidAmount();
					int capacity = well.getTank().getCapacity();
					boolean upsideDown = state.getValue(UPSIDE_DOWN);
					double height = getFluidHeight(amount, capacity, upsideDown);
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
	public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
		// improve the roof sound if possible (some mods change the sound type of bricks to be better)
		return state.getValue(HALF) == DoubleBlockHalf.UPPER ? this.soundType : Blocks.BRICKS.getSoundType(state, level, pos, entity);
	}
	
	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.BLOCK;
	}
	
	@Override
	protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
		return false;
	}
	
	@SuppressWarnings("deprecation")
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
		} else {
			return state.getValue(UPSIDE_DOWN) ? flipShapeUpsideDown(flipShapeXZ(SHAPE_ROOF)) : flipShapeXZ(SHAPE_ROOF);
		}
	}
	
	@Override
	public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
		if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
			return state.getValue(UPSIDE_DOWN) ? flipShapeUpsideDown(SHAPE_BASE) : SHAPE_BASE;
		} else if (state.getValue(AXIS) == Direction.Axis.X) {
			return state.getValue(UPSIDE_DOWN) ? flipShapeUpsideDown(SHAPE_INNER_SUPPORT) : SHAPE_INNER_SUPPORT;
		} else {
			return state.getValue(UPSIDE_DOWN) ? flipShapeUpsideDown(flipShapeXZ(SHAPE_INNER_SUPPORT)) : flipShapeXZ(SHAPE_INNER_SUPPORT);
		}
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
		} else {
			return state.getValue(UPSIDE_DOWN) ? flipShapeUpsideDown(flipShapeXZ(SHAPE_ROOF)) : flipShapeXZ(SHAPE_ROOF);
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return switch (rotation) {
			case COUNTERCLOCKWISE_90, CLOCKWISE_90 -> switch (state.getValue(AXIS)) {
				case Z -> state.setValue(AXIS, Direction.Axis.X);
				case X -> state.setValue(AXIS, Direction.Axis.Z);
				default -> state;
			};
			default -> state;
		};
	}
	
	/**
	 * Flips the VoxelShape from the X axis to the Z axis, or vice versa.
	 *
	 * @param shape the shape to be flipped
	 * @return the flipped VoxelShape
	 */
	public static VoxelShape flipShapeXZ(VoxelShape shape) {
		VoxelShape[] buffer = new VoxelShape[]{ shape, Shapes.empty() };
		buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(buffer[1], Shapes.create(minZ, minY, minX, maxZ, maxY, maxX)));
		
		return buffer[1];
	}
	
	/**
	 * Flips the VoxelShape upside down.
	 *
	 * @param shape the shape to be flipped
	 * @return the flipped VoxelShape
	 */
	public static VoxelShape flipShapeUpsideDown(VoxelShape shape) {
		VoxelShape[] buffer = new VoxelShape[]{ shape, Shapes.empty() };
		buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(buffer[1], Shapes.create(minX, 1 - maxY, minZ, maxX, 1 - minY, maxZ)));
		
		return buffer[1];
	}
	
	public static double getFluidHeight(int amount, int capacity, boolean upsideDown) {
		double height = amount * 14F / (16 * capacity) + (2F / 16);
		return upsideDown ? 1 - height : height;
	}
	
}
