package cubicoder.well.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

public class WellConfig {

	public static final ForgeConfigSpec CLIENT_CONFIG = clientConfig(new ForgeConfigSpec.Builder());
	public static final ForgeConfigSpec SERVER_CONFIG = serverConfig(new ForgeConfigSpec.Builder());
	
	public static ForgeConfigSpec.IntValue tankCapacity;
	public static ForgeConfigSpec.BooleanValue onlyOnePerChunk;
	public static ForgeConfigSpec.BooleanValue playSound;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> dataList;
	
	private static final List<WellData> regularWellDataList = new ArrayList<>();
	private static final List<WellData> upsideWellDataList = new ArrayList<>();
	
	public static void init() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG);
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG);
	}

	private static ForgeConfigSpec clientConfig(ForgeConfigSpec.Builder builder) {
		playSound = builder
				.comment("Play the well cranking sound when someone takes fluid from a well.")
				.translation("config.well.playSound")
				.define("playSound", true);
		return builder.build();
	}

	private static ForgeConfigSpec serverConfig(ForgeConfigSpec.Builder builder) {
		tankCapacity = builder
				.comment("How many millibuckets of a fluid can wells hold? Set to 0 to disable.")
				.translation("config.well.tankCapacity")
				.worldRestart()
				.defineInRange("tankCapacity", 100000, 0, Integer.MAX_VALUE);
		onlyOnePerChunk = builder
				.comment("When set to true, all wells in the chunk will stop working while there's more than 1.")
				.translation("config.well.onlyOnePerChunk")
				.define("onlyOnePerChunk", false);
		dataList = builder
				.comment("Handle what fluids wells collect based on biome, and how much")
				.translation("config.well.data")
				.defineListAllowEmpty(List.of("dataList"), () -> List.of("{}"), WellConfig::validateData);
		return builder.build();
	}
	
	public static boolean validateData(Object entry) {
		if (!(entry instanceof String)) return false;
		try {
			TagParser.parseTag((String) entry);
			return true;
		} catch (CommandSyntaxException e) {
			return false;
		}
	}
	
	public static void initData(String entry) {		
		try {
			CompoundTag data = TagParser.parseTag(entry);
			
			if (data.contains("Fluid", Tag.TAG_COMPOUND)) {
				FluidStack fluid = FluidStack.loadFluidStackFromNBT(data.getCompound("Fluid"));
				if (fluid == null) return;
				
				boolean isUpsideDown = fluid.getFluid().getAttributes().isLighterThanAir();
				
				// fill delays
				int minToFill = (data.contains("MinTicks", Tag.TAG_INT)
						? Math.abs(data.getInt("MinTicks"))
						: (isUpsideDown ? WellData.UPSIDE_DEFAULT : WellData.REGULAR_DEFAULT).minToFill);
				int maxToFill = (data.contains("MaxTicks", Tag.TAG_INT)
						? Math.abs(data.getInt("MaxTicks"))
						: (isUpsideDown ? WellData.UPSIDE_DEFAULT : WellData.REGULAR_DEFAULT).maxToFill);
				
				// handle biomes
				List<ResourceLocation> biomes = new ArrayList<>();
				data.getList("Biomes", Tag.TAG_STRING)
						.forEach(biomeNbt -> biomes.add(new ResourceLocation(biomeNbt.getAsString())));				
				
				// handle biome tags
				List<ResourceLocation> biomeTags = new ArrayList<>();
				data.getList("BiomeTags", Tag.TAG_STRING)
						.forEach(biomeTagNbt -> biomeTags.add(new ResourceLocation(biomeTagNbt.getAsString())));
				
				WellData wellData;
				if (biomes.isEmpty() && biomeTags.isEmpty()) {
					// if no biome info, treat this as a default definition
					wellData = new WellData(fluid, minToFill, maxToFill);
					if (isUpsideDown) {
						WellData.UPSIDE_DEFAULT.fluid = wellData.fluid;
						WellData.UPSIDE_DEFAULT.minToFill = wellData.minToFill;
						WellData.UPSIDE_DEFAULT.maxToFill = wellData.maxToFill;
					} else {
						WellData.REGULAR_DEFAULT.fluid = wellData.fluid;
						WellData.REGULAR_DEFAULT.minToFill = wellData.minToFill;
						WellData.REGULAR_DEFAULT.maxToFill = wellData.maxToFill;
					}
				} else {
					wellData = new WellData(fluid, minToFill, maxToFill, biomes, biomeTags);
					if (isUpsideDown) {
						upsideWellDataList.add(wellData);
					} else {
						regularWellDataList.add(wellData);
					}
				}
				
			}
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean canGenerateFluid(int nearbyWells) {
		return !onlyOnePerChunk.get() || nearbyWells == 1;
	}
	
	public static FluidStack getFillFluid(Biome biome, Level level, BlockPos pos, boolean upsideDown, int nearbyWells) {
		WellData data = getWellDataForBiome(biome, level, upsideDown);
		FluidStack fluid = data.fluid.copy();
		return fluid;
	}
	
	public static int getFillDelay(Biome biome, Level level, Random random, boolean upsideDown) {
		WellData data = getWellDataForBiome(biome, level, upsideDown);
		return Mth.nextInt(random, data.minToFill, data.maxToFill);
	}
	
	private static WellData getWellDataForBiome(Biome biome, Level level, boolean upsideDown) {
		for (WellData wellData : (upsideDown ? upsideWellDataList : regularWellDataList)) {
			if (wellData.hasBiome(biome, level)) return wellData;
		}
		return upsideDown ? WellData.UPSIDE_DEFAULT : WellData.REGULAR_DEFAULT;
	}
	
	public static void configChanged(ModConfigEvent event) {
		if (event.getConfig().getType() == ModConfig.Type.SERVER) {
			WellData.REGULAR_DEFAULT.resetToDefault();
			WellData.UPSIDE_DEFAULT.resetToDefault();
			regularWellDataList.clear();
			upsideWellDataList.clear();
			
			dataList.get().forEach(entry -> initData(entry));
		}
	}
	
}
