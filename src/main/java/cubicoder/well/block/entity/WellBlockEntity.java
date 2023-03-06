package cubicoder.well.block.entity;

import java.util.function.Consumer;

import cubicoder.well.block.ModBlocks;
import cubicoder.well.block.WellBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.TileFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class WellBlockEntity extends TileFluidHandler {

	public int fillTick = 0;
	public int nearbyWells = 1;
	public int delayUntilNextBucket = 0; // when filling an item from the well, delay before another can be filled
	
	public WellBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlocks.WELL_BE.get(), pos, state);
		tank.setCapacity(100000); // TODO config
	}
	
	public static void serverTick(Level level, BlockPos pos, BlockState state, WellBlockEntity be) {
		if (be.delayUntilNextBucket > 0) {
			be.delayUntilNextBucket--;
			be.setChanged();
		}
		
		if (be.fillTick > 0) {
			be.fillTick--;
			be.setChanged();
		}
		
		if (be.fillTick <= 0 /*&& ConfigHandler.canGenerateFluid(nearbyWells)*/) { // TODO config
			FluidStack fluidToFill = /*getFluidToFill()*/ new FluidStack(Fluids.WATER, 1000); // TODO config
			if (fluidToFill != null) be.tank.fill(fluidToFill, FluidAction.EXECUTE);
			be.initFillTick();
			be.setChanged();
		}
	}

	@Override
	public void onLoad() {
		if (!level.isClientSide) {
			initFillTick();
			countNearbyWells(be -> {
				be.nearbyWells++;
				this.nearbyWells++;
			});
		}
		
		//if (((FluidTankSynced) tank).updateLight(tank.getFluid())) level.setBlocksDirty(getBlockPos(), getBlockState(), getBlockState());
	}
	
	// TODO config
	/*protected FluidStack getFluidToFill() {
		return ConfigHandler.getFillFluid(getBiome(), level, isUpsideDown(), nearbyWells);
	}*/
	
	protected void initFillTick() {
		//fillTick = ConfigHandler.getFillDelay(getBiome(), level.random, isUpsideDown()); // TODO config
		fillTick = 40; // two seconds
	}
	
	public void countNearbyWells(Consumer<WellBlockEntity> updateScript) {
		level.getChunkAt(getBlockPos()).getBlockEntitiesPos().forEach(otherPos -> {
			// idk if I like this biome check - it should just be per chunk, straight up
			if(!otherPos.equals(getBlockPos())/* && level.getBiome(otherPos).value() == getBiome()*/) {
				BlockEntity be = level.getBlockEntity(otherPos);
				if (be instanceof WellBlockEntity && ((WellBlockEntity) be).isUpsideDown() == isUpsideDown()) {
					updateScript.accept((WellBlockEntity) be);
				}
			}
		});
	}
	
	public boolean isUpsideDown() {
		return this.getBlockState().getValue(WellBlock.UPSIDE_DOWN);
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		fillTick = tag.getInt("FillTick");
		nearbyWells = Math.max(1, tag.getInt("nearbyWells"));
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("FillTick", fillTick);
		tag.putInt("NearbyWells", nearbyWells);
	}
	
	// TODO rendering stuff
	/*@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return super.getUpdatePacket();
	}
	
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		super.onDataPacket(net, pkt);
	}*/
	
	public FluidTank getTank() {
		return tank;
	}
	
}
