package cubicoder.well.block.entity;

import cubicoder.well.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.TileFluidHandler;

public class WellBlockEntity extends TileFluidHandler {

	public WellBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlocks.WELL_BE.get(), pos, state);
	}
	
	

}
