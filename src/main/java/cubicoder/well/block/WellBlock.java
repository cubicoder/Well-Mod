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
	// TODO this looks janky af and a regular box might be better
	public static final VoxelShape SHAPE_ROOF = Shapes.or(
			Block.box(7.5D, 0.0D, 1.0D, 8.5D, 15.0D, 2.0D),
			Block.box(7.5D, 0.0D, 14.0D, 8.5D, 15.0D, 15.0D),
			Block.box(7.5D, 7.0D, 2.0D, 8.5D, 8.0D, 14.0D),
			Block.box(5.0D, 4.5D, 4.5D, 11.0D, 10.5D, 11.5D),
			Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D)
	);
	
	public WellBlock(MaterialColor mapColor) {
		this(Material.STONE, mapColor);
	}
	
	public WellBlock(Material material, MaterialColor mapColor) {
		this(Properties.of(material).color(mapColor).strength(3.0F, 1.5F).requiresCorrectToolForDrops().noOcclusion());
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
			}/* else {
				dropResources(state, level, pos, level.getBlockEntity(pos), player, player.getMainHandItem());
			}*/
		}

		super.playerWillDestroy(level, pos, state, player);
	}
	
	// if playerWillDestroy works as is, then this is not needed
	/*@Override
	public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool) {
		super.playerDestroy(level, player, pos, Blocks.AIR.defaultBlockState(), blockEntity, tool);
	}*/
	
	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.BLOCK;
	}
	
	@Override
	public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
		return false;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return state.getValue(HALF) == DoubleBlockHalf.LOWER ? SHAPE_BASE : SHAPE_ROOF;
	}
	
	/*@Override
	public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
		return null;
	}*/
	
	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}
	
	// TODO needed?
	/*@Override
	public boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction dir) {
		return (state.getValue(HALF) == DoubleBlockHalf.UPPER) && dir != Direction.UP;
	}*/
	
}
