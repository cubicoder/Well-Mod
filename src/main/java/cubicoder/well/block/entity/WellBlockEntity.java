package cubicoder.well.block.entity;

import cubicoder.well.block.ModBlocks;
import cubicoder.well.block.WellBlock;
import cubicoder.well.config.WellConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.function.Consumer;

public class WellBlockEntity extends BlockEntity {

	public int fillTick = 0;
	public int nearbyWells = 1;
	public int delayUntilNextBucket = 0; // when filling an item from the well, delay before another can be filled
	private WellFluidTank tank;

	public WellBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlocks.WELL_BE.get(), pos, state);
		tank = new WellFluidTank(this, WellConfig.tankCapacity.get());
	}
	
	public static void serverTick(Level level, BlockPos pos, BlockState state, WellBlockEntity well) {
		if (well.delayUntilNextBucket > 0) {
			well.delayUntilNextBucket--;
		}
		
		if (well.fillTick > 0) {
			well.fillTick--;
			well.setChanged();
		}
		
		if (well.fillTick <= 0 && WellConfig.canGenerateFluid(well.nearbyWells)) {
			FluidStack fluidToFill = well.getFluidToFill();
			int result = 0;
			if (fluidToFill != null) {
				result = well.tank.fill(fluidToFill, IFluidHandler.FluidAction.EXECUTE);
			}
			if (result > 0) {
				well.initFillTick();
				well.setChanged();
			}
		}
	}

	@Override
	public void onLoad() {
		if (tank.updateLight(tank.getFluid())) {
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
		}
	}

	protected FluidStack getFluidToFill() {
		return WellConfig.getFillFluid(level.getBiome(getBlockPos()).value(), level, isUpsideDown());
	}

	public void initFillTick() {
		fillTick = WellConfig.getFillDelay(level.getBiome(getBlockPos()).value(), level, level.random, isUpsideDown());
	}
	
	public void countNearbyWells(Consumer<WellBlockEntity> updateScript) {
		level.getChunkAt(getBlockPos()).getBlockEntitiesPos().forEach(otherPos -> {
			if(!otherPos.equals(getBlockPos())) {
				BlockEntity be = level.getBlockEntity(otherPos);
				if (be instanceof WellBlockEntity well && well.isUpsideDown() == isUpsideDown()) {
					updateScript.accept(well);
				}
			}
		});
	}
	
	public boolean isUpsideDown() {
		return this.getBlockState().getValue(WellBlock.UPSIDE_DOWN);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		fillTick = tag.getInt("FillTick");
		nearbyWells = Math.max(1, tag.getInt("NearbyWells"));
		tank.readFromNBT(registries, tag);
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putInt("FillTick", fillTick);
		tag.putInt("NearbyWells", nearbyWells);
		tank.writeToNBT(registries, tag);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		return saveWithoutMetadata(registries);
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
		FluidStack oldFluid = tank.getFluid();
		handleUpdateTag(pkt.getTag(), lookupProvider);
		FluidStack newFluid = tank.getFluid();

		boolean wasEmpty = newFluid != null && oldFluid == null;
		boolean wasFull = newFluid == null && oldFluid != null;

		// update renderer and light level if needed
		if (wasEmpty || wasFull || newFluid != null && newFluid.getAmount() != oldFluid.getAmount()) {
			if (newFluid != null) tank.updateLight(newFluid);
			else tank.updateLight(oldFluid);
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
		}
	}
	
	public FluidTank getTank() {
		return tank;
	}

	public static class WellFluidTank extends FluidTank {

		private WellBlockEntity well;
		
		public WellFluidTank(WellBlockEntity well, int capacity) {
			super(capacity);
			this.well = well;
			setValidator(fluid -> {
				// well is upside down, only allow upside down fluids, or vice versa
				boolean isLighterThanAir = fluid.getFluid().getFluidType().isLighterThanAir();
				if (this.well.isUpsideDown()) {
					return isLighterThanAir;
				} else if (isLighterThanAir) return false;
				
				// no fluids that evaporate
				if (this.well.getLevel().dimensionType().ultraWarm() && fluid.getFluid().getFluidType()
						.isVaporizedOnPlacement(this.well.getLevel(), this.well.getBlockPos(), fluid))
					return false;
				
				return true;
			});
		}
		
		@Override
		public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
			int fill = well.getFluidToFill().getFluid() == resource.getFluid() ? super.fill(resource, action) : 0;
			if (action.execute() && fill > 0) {
				BlockState state = well.getBlockState();
				well.getLevel().sendBlockUpdated(well.getBlockPos(), state, state, Block.UPDATE_ALL);
				updateLight(resource);
			}
			return fill;
		}
		
		@Override
		public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
			FluidStack resource = super.drain(maxDrain, action);
			if (resource != null && action.execute()) {
				BlockState state = well.getBlockState();
				well.getLevel().sendBlockUpdated(well.getBlockPos(), state, state, Block.UPDATE_ALL);
				updateLight(resource);
			}
			return resource;
		}
		
		protected boolean updateLight(FluidStack resource) {
			if (resource != null) {
				if (resource.getFluid().getFluidType().getLightLevel(resource.getFluid().defaultFluidState(),
						well.getLevel(), well.getBlockPos()) > 0) {
					well.getLevel().getLightEngine().checkBlock(well.getBlockPos());
					return true;
				}
			}
			return false;
		}
		
	}
	
}
