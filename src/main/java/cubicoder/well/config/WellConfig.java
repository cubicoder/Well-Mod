package cubicoder.well.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

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
				.defineListAllowEmpty(Collections.singletonList("dataList"), () -> Collections.singletonList("{}"), WellConfig::validateData);
		return builder.build();
	}
	
	public static boolean validateData(Object entry) {
		if (!(entry instanceof String)) return false;
		try {
			JsonToNBT.parseTag((String) entry);
			return true;
		} catch (CommandSyntaxException e) {
			return false;
		}
	}
	
	public static void initData(String entry) {		
		try {
			CompoundNBT data = JsonToNBT.parseTag(entry);
			
			if (data.contains("Fluid", NBT.TAG_COMPOUND)) {
				FluidStack fluid = FluidStack.loadFluidStackFromNBT(data.getCompound("Fluid"));
				if (fluid == null) return;
				
				boolean isUpsideDown = fluid.getFluid().getAttributes().isLighterThanAir();
				
				// fill delays
				int minToFill = (data.contains("MinTicks", NBT.TAG_INT)
						? Math.abs(data.getInt("MinTicks"))
						: (isUpsideDown ? WellData.UPSIDE_DEFAULT : WellData.REGULAR_DEFAULT).minToFill);
				int maxToFill = (data.contains("MaxTicks", NBT.TAG_INT)
						? Math.abs(data.getInt("MaxTicks"))
						: (isUpsideDown ? WellData.UPSIDE_DEFAULT : WellData.REGULAR_DEFAULT).maxToFill);
				
				// handle biomes
				List<Biome> biomes = new ArrayList<>();
				data.getList("Biomes", NBT.TAG_STRING).forEach(biomeNbt -> biomes
						.add(ForgeRegistries.BIOMES.getValue(new ResourceLocation(biomeNbt.getAsString()))));
				
				// handle biome tags
				List<BiomeDictionary.Type> biomeTags = new ArrayList<>();
				data.getList("BiomeTags", NBT.TAG_STRING)
						.forEach(biomeTagNbt -> biomeTags.add(BiomeDictionary.Type.getType(biomeTagNbt.getAsString())));
				
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
	
	public static FluidStack getFillFluid(Biome biome, World level, BlockPos pos, boolean upsideDown, int nearbyWells) {
		WellData data = getWellDataForBiome(biome, level, upsideDown);
		FluidStack fluid = data.fluid.copy();
		return fluid;
	}
	
	public static int getFillDelay(Biome biome, World level, Random random, boolean upsideDown) {
		WellData data = getWellDataForBiome(biome, level, upsideDown);
		return MathHelper.nextInt(random, data.minToFill, data.maxToFill);
	}
	
	private static WellData getWellDataForBiome(Biome biome, World level, boolean upsideDown) {
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
