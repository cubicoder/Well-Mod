package cubicoder.well.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WellBlock extends BaseEntityBlock {

	public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
	public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
	public static final BooleanProperty UPSIDE_DOWN = BooleanProperty.create("upside_down");
	
	public static final VoxelShape SHAPE_BASE = Shapes.join(Shapes.block(), Block.box(3.0D, 2.0D, 3.0D, 13.0D, 16.0D, 13.0D), BooleanOp.ONLY_FIRST);
	public static final VoxelShape SHAPE_INNER_SUPPORT_X = Shapes.or(
			Block.box(7.5D, 0.0D, 1.0D, 8.5D, 15.0D, 2.0D),
			Block.box(7.5D, 0.0D, 14.0D, 8.5D, 15.0D, 15.0D),
			Block.box(7.5D, 7.0D, 2.0D, 8.5D, 8.0D, 14.0D),
			Block.box(5.0D, 4.5D, 4.5D, 11.0D, 10.5D, 11.5D)
	);
	public static final VoxelShape SHAPE_INNER_SUPPORT_Z = Shapes.or(
			Block.box(1.0D, 0.0D, 7.5D, 2.0D, 15.0D, 8.5D),
			Block.box(14.0D, 0.0D, 7.5D, 15.0D, 15.0D, 8.5D),
			Block.box(2.0D, 7.0D, 7.5D, 14.0D, 8.0D, 8.5D),
			Block.box(4.5D, 4.5D, 5.0D, 11.5D, 10.5D, 11.0D)
	);
	public static final VoxelShape SHAPE_ROOF_X = Shapes.or(
			Block.box(5.5D, 12.5D, 0.0D, 10.5D, 15.707D, 16.0D),
			Block.box(2.75D, 10.5D, 0.0D, 5.5D, 13.75D, 16.0D),
			Block.box(10.5D, 10.5D, 0.0D, 13.25D, 13.75D, 16.0D),
			Block.box(0.0D, 8.0D, 0.0D, 2.75D, 11.25D, 16.0D),
			Block.box(13.25D, 8.0D, 0.0D, 16.0D, 11.25D, 16.0D),
			SHAPE_INNER_SUPPORT_X
	);
	public static final VoxelShape SHAPE_ROOF_Z = Shapes.or(
			Block.box(0.0D, 12.5D, 5.5D, 16.0D, 15.707D, 10.5D),
			Block.box(0.0D, 10.5D, 2.75D, 16.0D, 13.75D, 5.5D),
			Block.box(0.0D, 10.5D, 10.5D, 16.0D, 13.75D, 13.25D),
			Block.box(0.0D, 8.0D, 0.0D, 16.0D, 11.25D, 2.75D),
			Block.box(0.0D, 8.0D, 13.25D, 16.0D, 11.25D, 16.0D),
			SHAPE_INNER_SUPPORT_Z
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
		return null; // TODO add block entity
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
		/*if (ConfigHandler.onlyOnePerChunk && placer instanceof ServerPlayer) {
			BlockEntity be = level.getBlockEntity(pos);
			if (be instanceof WellBlockEntity && ((WellBlockEntity) be).nearbyWells > 1) {
				// TODO stop block from being placed if there's only one well per area? instead of just warning
				String message = state.getValue(UPSIDE_DOWN) ? "warn.well.onePerChunkFlipped" : "warn.well.onePerChunk";
				placer.displayClientMessage(new TranslatableComponent(message), true);
			}
				
		}*/
	}
	
	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
			return super.canSurvive(state, level, pos);
		} else {
			BlockState base = level.getBlockState(pos.below(state.getValue(UPSIDE_DOWN) ? -1 : 1));
			return base.is(this) && base.getValue(HALF) == DoubleBlockHalf.UPPER;
		}
	}
	
	@Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		if (!level.isClientSide) {
			if (player.isCreative()) {
				if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
					BlockPos otherPos = pos.below(state.getValue(UPSIDE_DOWN) ? -1 : 1);
					BlockState otherState = level.getBlockState(otherPos);
					if (otherState.is(this) && otherState.getValue(HALF) == DoubleBlockHalf.LOWER) {
						level.setBlock(otherPos, Blocks.AIR.defaultBlockState(), 35);
					}
				}
			}
		}

		super.playerWillDestroy(level, pos, state, player);
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
			return SHAPE_BASE;
		} else if (state.getValue(AXIS) == Direction.Axis.X) {
			return SHAPE_ROOF_X;
		} else return SHAPE_ROOF_Z;		
	}
	
	@Override
	public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
		if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
			return SHAPE_BASE;
		} else if (state.getValue(AXIS) == Direction.Axis.X) {
			return SHAPE_INNER_SUPPORT_X;
		} else return SHAPE_INNER_SUPPORT_Z;
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
			return SHAPE_ROOF_X;
		} else return SHAPE_ROOF_Z;	
	}
	
}
