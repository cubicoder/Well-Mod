package cubicoder.well.block.entity;

import java.util.function.Consumer;

import cubicoder.well.block.ModBlocks;
import cubicoder.well.block.WellBlock;
import cubicoder.well.config.WellConfig;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants.BlockFlags;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.TileFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class WellBlockEntity extends TileFluidHandler implements ITickableTileEntity {

	public int fillTick = 0;
	public int nearbyWells = 1;
	public int delayUntilNextBucket = 0; // when filling an item from the well, delay before another can be filled
	public boolean initialized;
	
	public WellBlockEntity() {
		super(ModBlocks.WELL_BE.get());
		tank = new WellFluidTank(this, WellConfig.tankCapacity.get());
	}
	
	public void tick() {
		if (delayUntilNextBucket > 0) {
			delayUntilNextBucket--;
		}
		
		if (fillTick > 0) {
			fillTick--;
			setChanged();
		}
		
		if (fillTick <= 0 && WellConfig.canGenerateFluid(nearbyWells)) {
			FluidStack fluidToFill = getFluidToFill();
			int result = 0;
			if (fluidToFill != null) {
				result = tank.fill(fluidToFill, FluidAction.EXECUTE);
			}
			if (result > 0) {
				initFillTick();
				setChanged();
			}
		}
	}

	@Override
	public void onLoad() {
		if (!initialized) {
			initialized = true;
			if (!level.isClientSide) {
				initFillTick();
				countNearbyWells(be -> {
					be.nearbyWells++;
					this.nearbyWells++;
				});
			}
		}
		
		if (((WellFluidTank) tank).updateLight(tank.getFluid())) {
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), BlockFlags.DEFAULT);
		}
	}
	
	protected FluidStack getFluidToFill() {
		return WellConfig.getFillFluid(level.getBiome(getBlockPos()), level, getBlockPos(), isUpsideDown(), nearbyWells);
	}
	
	protected void initFillTick() {
		fillTick = WellConfig.getFillDelay(level.getBiome(getBlockPos()), level, level.random, isUpsideDown());
	}
	
	public void countNearbyWells(Consumer<WellBlockEntity> updateScript) {
		level.getChunkAt(getBlockPos()).getBlockEntitiesPos().forEach(otherPos -> {
			if(!otherPos.equals(getBlockPos())) {
				TileEntity be = level.getBlockEntity(otherPos);
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
	public void load(BlockState state, CompoundNBT tag) {
		super.load(state, tag);
		fillTick = tag.getInt("FillTick");
		nearbyWells = Math.max(1, tag.getInt("NearbyWells"));
		initialized = tag.getBoolean("Initialized");
	}
	
	@Override
	public CompoundNBT save(CompoundNBT tag) {
		tag = super.save(tag);
		tag.putInt("FillTick", fillTick);
		tag.putInt("NearbyWells", nearbyWells);
		tag.putBoolean("Initialized", initialized);
		return tag;
	}
	
	@Override
	public CompoundNBT getUpdateTag() {
		return this.save(new CompoundNBT());
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(getBlockPos(), -1, this.getUpdateTag());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		FluidStack oldFluid = tank.getFluid();
		handleUpdateTag(getBlockState(), pkt.getTag());
		FluidStack newFluid = tank.getFluid();
		
		boolean wasEmpty = newFluid != null && oldFluid == null;
		boolean wasFull = newFluid == null && oldFluid != null;

		// update renderer and light level if needed
		if (wasEmpty || wasFull || newFluid != null && newFluid.getAmount() != oldFluid.getAmount()) {
			if (newFluid != null) ((WellFluidTank) tank).updateLight(newFluid);
			else ((WellFluidTank) tank).updateLight(oldFluid);
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), BlockFlags.DEFAULT);
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
			int fill = well.getFluidToFill().getFluid() == resource.getFluid() ? super.fill(resource, action) : 0;
			if (action.execute() && fill > 0) {
				BlockState state = well.getBlockState();
				well.getLevel().sendBlockUpdated(well.getBlockPos(), state, state, BlockFlags.DEFAULT);
				updateLight(resource);
			}
			return fill;
		}
		
		@Override
		public FluidStack drain(int maxDrain, FluidAction action) {
			FluidStack resource = super.drain(maxDrain, action);
			if (resource != null && action.execute()) {
				BlockState state = well.getBlockState();
				well.getLevel().sendBlockUpdated(well.getBlockPos(), state, state, BlockFlags.DEFAULT);
				updateLight(resource);
			}
			return resource;
		}
		
		protected boolean updateLight(FluidStack resource) {
			if (resource != null) {
				if (resource.getFluid().getAttributes().getLuminosity() > 0) {
					well.getLevel().getLightEngine().checkBlock(well.getBlockPos());
					return true;
				}
			}
			return false;
		}
		
	}
	
}
