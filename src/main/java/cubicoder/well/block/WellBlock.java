package cubicoder.well.block;

import java.util.Random;

import cubicoder.well.block.entity.WellBlockEntity;
import cubicoder.well.config.WellConfig;
import cubicoder.well.sound.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class WellBlock extends Block {

	public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
	public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
	public static final BooleanProperty UPSIDE_DOWN = BooleanProperty.create("upside_down");
	
	public static final VoxelShape SHAPE_BASE = VoxelShapes.join(VoxelShapes.block(), Block.box(3.0D, 2.0D, 3.0D, 13.0D, 16.0D, 13.0D), IBooleanFunction.ONLY_FIRST);
	public static final VoxelShape SHAPE_INNER_SUPPORT = VoxelShapes.or(
			Block.box(7.5D, 0.0D, 1.0D, 8.5D, 15.0D, 2.0D),
			Block.box(7.5D, 0.0D, 14.0D, 8.5D, 15.0D, 15.0D),
			Block.box(7.5D, 7.0D, 2.0D, 8.5D, 8.0D, 14.0D),
			Block.box(5.0D, 4.5D, 4.5D, 11.0D, 10.5D, 11.5D)
	);
	public static final VoxelShape SHAPE_ROOF = VoxelShapes.or(
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
		this(Properties.of(material, mapColor).strength(3.0F, 1.5F).harvestLevel(0).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops());
	}
	
	public WellBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any()
				.setValue(AXIS, Direction.Axis.X)
				.setValue(HALF, DoubleBlockHalf.LOWER)
				.setValue(UPSIDE_DOWN, false));
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return state.getValue(HALF) == DoubleBlockHalf.LOWER;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new WellBlockEntity();
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(AXIS, HALF, UPSIDE_DOWN);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		World level = context.getLevel();
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
		if (pos.getY() >= 0) {
			if (level.getBlockState(pos.below()).canBeReplaced(context)) {
				return this.defaultBlockState().setValue(AXIS, axis).setValue(UPSIDE_DOWN, true);
			}
		}
		
		return null;
	}
		
	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, IWorld level, BlockPos currentPos, BlockPos neighborPos) {
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
	public void setPlacedBy(World level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		level.setBlockAndUpdate(pos.above(state.getValue(UPSIDE_DOWN) ? -1 : 1), state.setValue(HALF, DoubleBlockHalf.UPPER));
		
		// warn placer if only one well can function in the area
		if (WellConfig.onlyOnePerChunk.get() && placer instanceof ServerPlayerEntity) {
			TileEntity be = level.getBlockEntity(pos);
			if (be instanceof WellBlockEntity && ((WellBlockEntity) be).nearbyWells > 1) {
				String message = state.getValue(UPSIDE_DOWN) ? "warn.well.onePerChunkFlipped" : "warn.well.onePerChunk";
				((ServerPlayerEntity) placer).displayClientMessage(new TranslationTextComponent(message), true);
			}
				
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean canSurvive(BlockState state, IWorldReader level, BlockPos pos) {
		if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
			return super.canSurvive(state, level, pos);
		} else {
			BlockState base = level.getBlockState(pos.below(state.getValue(UPSIDE_DOWN) ? -1 : 1));
			if (state.getBlock() != this) return super.canSurvive(state, level, pos);
			return base.is(this) && base.getValue(HALF) == DoubleBlockHalf.LOWER;
		}
	}
		
	@Override
	public void playerWillDestroy(World level, BlockPos pos, BlockState state, PlayerEntity player) {
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
	public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			TileEntity be = level.getBlockEntity(pos);
			if (be instanceof WellBlockEntity) {
				((WellBlockEntity) be).countNearbyWells(e -> e.nearbyWells--);
			}
			super.onRemove(state, level, pos, newState, isMoving);
		}
	}
		
	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
			return ActionResultType.PASS;
		}
		
		if (!player.getItemInHand(hand).getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()) {
			return ActionResultType.PASS;
		}
		
		if (level.isClientSide) {
			return ActionResultType.SUCCESS;
		}
		
		TileEntity be = level.getBlockEntity(pos);
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
					level.playSound(null, pos.above(), ModSounds.CRANK.get(), SoundCategory.BLOCKS, 0.25F, 1);
					well.delayUntilNextBucket = 32;
				}
				return ActionResultType.SUCCESS;
			}
		}
		
		return ActionResultType.PASS;
	}
	
	@Override
	public int getLightValue(BlockState state, IBlockReader level, BlockPos pos) {
		if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
			TileEntity be = level.getBlockEntity(pos);
			if (be instanceof WellBlockEntity) {
				FluidStack fluid = ((WellBlockEntity) be).getTank().getFluid();
				if (fluid != null && !fluid.isEmpty()) {
					int baseFluidLight = fluid.getFluid().getAttributes().getLuminosity();
					if (baseFluidLight > 0) {
						return MathHelper.clamp((int) (baseFluidLight * fluid.getAmount() / WellConfig.tankCapacity.get() + 0.5), 1, 15);
					}
				}
			}
		}
		
		return 0;
	}
	
	@Override
	public void entityInside(BlockState state, World level, BlockPos pos, Entity entity) {
		if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
			TileEntity be = level.getBlockEntity(pos);
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
								if (!entity.fireImmune()) {
							         entity.setSecondsOnFire(15);
							         entity.hurt(DamageSource.LAVA, 4.0F);
							      }
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
	public void animateTick(BlockState state, World level, BlockPos pos, Random random) {
		if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
			TileEntity be = level.getBlockEntity(pos);
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
							level.playLocalSound(x, y, z, SoundEvents.LAVA_POP, SoundCategory.BLOCKS,
									0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
						}

						if (random.nextInt(200) == 0) {
							double x = (double) pos.getX() + 0.5;
							double y = (double) pos.getY() + height / 2;
							double z = (double) pos.getZ() + 0.5;
							level.playLocalSound(x, y, z, SoundEvents.LAVA_AMBIENT, SoundCategory.BLOCKS,
									0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
						}
					}
				}
			}
		}
	}
	
	@Override
	public SoundType getSoundType(BlockState state, IWorldReader level, BlockPos pos, Entity entity) {
		// improve the roof sound if possible (some mods change the sound type of bricks to be better)
		return state.getValue(HALF) == DoubleBlockHalf.UPPER ? this.soundType : Blocks.BRICKS.getSoundType(state, level, pos, entity);
	}
		
	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.BLOCK;
	}
	
	@Override
	public boolean isPathfindable(BlockState state, IBlockReader level, BlockPos pos, PathType type) {
		return false;
	}
	
	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
		if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
			return state.getValue(UPSIDE_DOWN) ? flipShapeUpsideDown(SHAPE_BASE) : SHAPE_BASE;
		} else if (state.getValue(AXIS) == Direction.Axis.X) {
			return state.getValue(UPSIDE_DOWN) ? flipShapeUpsideDown(SHAPE_ROOF) : SHAPE_ROOF;
		} else return state.getValue(UPSIDE_DOWN) ? flipShapeUpsideDown(flipShapeXZ(SHAPE_ROOF)) : flipShapeXZ(SHAPE_ROOF);
	}
	
	@Override
	public VoxelShape getOcclusionShape(BlockState state, IBlockReader level, BlockPos pos) {
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
	public VoxelShape getInteractionShape(BlockState state, IBlockReader level, BlockPos pos) {
		if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
			return VoxelShapes.block();
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
		VoxelShape[] buffer = new VoxelShape[] { shape, VoxelShapes.empty() };
		buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
			buffer[1] = VoxelShapes.or(buffer[1], VoxelShapes.create(new AxisAlignedBB(minZ, minY, minX, maxZ, maxY, maxX)));
		});
		
		return buffer[1];
	}
	
	/**
	 * Flips the VoxelShape upside down.
	 * @param shape the shape to be flipped
	 * @return the flipped VoxelShape
	 */
	public static VoxelShape flipShapeUpsideDown(VoxelShape shape) {
		VoxelShape[] buffer = new VoxelShape[] { shape, VoxelShapes.empty() };
		buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
			buffer[1] = VoxelShapes.or(buffer[1], VoxelShapes.create(new AxisAlignedBB(minX, 1 - maxY, minZ, maxX, 1 - minY, maxZ)));
		});
		
		return buffer[1];
	}
	
	public static float getFluidRenderHeight(int amount, int capacity, boolean upsideDown) {
		float height = amount * 14F / (16 * capacity) + (2F / 16);
		return upsideDown ? 1 - height : height;
	}

}
