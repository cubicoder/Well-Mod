package cubicoder.well.block.entity;

import java.util.function.Consumer;

import cubicoder.well.block.ModBlocks;
import cubicoder.well.block.WellBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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
		tank = new WellFluidTank(this, 100000); // TODO config
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
			FluidStack fluidToFill = be.getFluidToFill();
			int result = 0;
			if (fluidToFill != null) {
				result = be.tank.fill(fluidToFill, FluidAction.EXECUTE);
			}
			if (result > 0) {
				be.initFillTick();
				be.setChanged();
			}
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
	protected FluidStack getFluidToFill() {
		return new FluidStack(Fluids.WATER, 1000);
		//return ConfigHandler.getFillFluid(getBiome(), level, isUpsideDown(), nearbyWells);
	}
	
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
		nearbyWells = Math.max(1, tag.getInt("NearbyWells"));
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("FillTick", fillTick);
		tag.putInt("NearbyWells", nearbyWells);
	}
	
	@Override
	public CompoundTag getUpdateTag() {
		return saveWithoutMetadata();
	}
	
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		FluidStack oldFluid = tank.getFluid();
		handleUpdateTag(pkt.getTag());
		FluidStack newFluid = tank.getFluid();
		
		boolean wasEmpty = newFluid != null && oldFluid == null;
		boolean wasFull = newFluid == null && oldFluid != null;

		// update renderer and light level if needed
		if (wasEmpty || wasFull || newFluid != null && newFluid.getAmount() != oldFluid.getAmount()) {
			//if (newFluid != null) ((FluidTankSynced) tank.updateLight(newFluid);
			//else ((FluidTankSynced) tank).updateLight(oldFluid);
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
		}
	}
	
	public FluidTank getTank() {
		return tank;
	}
	
	// TODO light stuff
	public static class WellFluidTank extends FluidTank {

		private WellBlockEntity well;
		
		public WellFluidTank(WellBlockEntity well, int capacity) {
			super(capacity);
			this.well = well;
			setValidator(fluid -> {
				// well is upside down, only allow upside down fluids, or vice versa
				boolean isLighterThanAir = fluid.getFluid().getAttributes().isLighterThanAir();
				if (this.well.isUpsideDown()) {
					return isLighterThanAir;
				} else if (isLighterThanAir) return false;
				
				// no fluids that evaporate
				if (this.well.getLevel().dimensionType().ultraWarm() && fluid.getFluid().getAttributes()
						.doesVaporize(this.well.getLevel(), this.well.getBlockPos(), fluid))
					return false;
				
				return true;
			});
		}
		
		@Override
		public int fill(FluidStack resource, FluidAction action) {
			// TODO stop fill from every source except the tick?
			int fill = super.fill(resource, action);
			if (action.execute() && fill > 0) {
				BlockState state = well.getBlockState();
				well.getLevel().sendBlockUpdated(well.getBlockPos(), state, state, Block.UPDATE_ALL);
				//updateLight(resource);
			}
			return fill;
		}
		
		@Override
		public FluidStack drain(int maxDrain, FluidAction action) {
			FluidStack resource = super.drain(maxDrain, action);
			if (resource != null && action.execute()) {
				BlockState state = well.getBlockState();
				well.getLevel().sendBlockUpdated(well.getBlockPos(), state, state, Block.UPDATE_ALL);
				//updateLight(resource);
			}
			return resource;
		}
		
	}
	
}
