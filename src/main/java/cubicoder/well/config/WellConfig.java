package cubicoder.well.config;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import cubicoder.well.WellMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class WellConfig {

	private static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();
	private static final ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();
	
	private static final List<WellData> regularWellDataList = new ArrayList<>();
	private static final List<WellData> upsideWellDataList = new ArrayList<>();
	
	public static ModConfigSpec.BooleanValue playSound = CLIENT_BUILDER
			.comment("Play the well cranking sound when someone takes fluid from a well.")
			.translation("config.well.playSound")
			.define("playSound", true);
	
	public static ModConfigSpec.IntValue tankCapacity = SERVER_BUILDER
			.comment("How many millibuckets of a fluid can wells hold? Set to 0 to disable.")
			.translation("config.well.tankCapacity")
			.worldRestart()
			.defineInRange("tankCapacity", 100000, 0, Integer.MAX_VALUE);
	public static ModConfigSpec.BooleanValue onlyOnePerChunk = SERVER_BUILDER
			.comment("When set to true, all wells in the chunk will stop working while there's more than 1.")
			.translation("config.well.onlyOnePerChunk")
			.define("onlyOnePerChunk", false);
	public static ModConfigSpec.ConfigValue<List<? extends String>> dataList = SERVER_BUILDER
			.comment("Handle what fluids wells collect based on biome, and how much")
			.translation("config.well.data")
			.defineListAllowEmpty(List.of("dataList"), () -> List.of("{}"), WellConfig::validateData);
	
	private WellConfig() {}
	
	public static void init(ModContainer modContainer) {
		modContainer.registerConfig(ModConfig.Type.CLIENT, CLIENT_BUILDER.build());
		modContainer.registerConfig(ModConfig.Type.SERVER, SERVER_BUILDER.build());
	}
	
	private static boolean validateData(Object entry) {
		if (!(entry instanceof String)) return false;
		try {
			TagParser.parseTag((String) entry);
			return true;
		} catch (CommandSyntaxException e) {
			return false;
		}
	}

	// from old version of FluidStack, since they removed it
	// seems like FluidStacks use Codecs and/or data components now, but this should work because
	//     my use of NBT here is all internal to the config
	private static FluidStack loadFluidStackFromNBT(CompoundTag nbt) {
		if (!nbt.contains("FluidName", Tag.TAG_STRING)) {
			return FluidStack.EMPTY;
		}

		ResourceLocation fluidName = ResourceLocation.parse(nbt.getString("FluidName"));
		Fluid fluid = BuiltInRegistries.FLUID.get(fluidName);
		if (fluid == Fluids.EMPTY) {
			return FluidStack.EMPTY;
		}
		
		return new FluidStack(fluid, nbt.getInt("Amount"));
	}
	
	private static void initData(String entry) {
		CompoundTag data;
		try {
			data = TagParser.parseTag(entry);
		} catch (CommandSyntaxException e) {
			WellMod.LOGGER.info("Failed to parse well data entry: {}", entry);
			return;
		}
		
		if (data.contains("Fluid", Tag.TAG_COMPOUND)) {
			FluidStack fluid = loadFluidStackFromNBT(data.getCompound("Fluid"));
			if (fluid.isEmpty()) return;
			
			boolean isUpsideDown = fluid.getFluid().getFluidType().isLighterThanAir();
			
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
					.forEach(biomeNbt -> biomes.add(ResourceLocation.parse(biomeNbt.getAsString())));
			
			// handle biome tags
			List<ResourceLocation> biomeTags = new ArrayList<>();
			data.getList("BiomeTags", Tag.TAG_STRING)
					.forEach(biomeTagNbt -> biomeTags.add(ResourceLocation.parse(biomeTagNbt.getAsString())));
			
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
	}
	
	public static boolean canGenerateFluid(int nearbyWells) {
		return nearbyWells == 1 || !onlyOnePerChunk.get();
	}
	
	private static WellData getWellDataForBiome(Biome biome, Level level, boolean upsideDown) {
		for (WellData wellData : (upsideDown ? upsideWellDataList : regularWellDataList)) {
			if (wellData.hasBiome(biome, level)) return wellData;
		}
		return upsideDown ? WellData.UPSIDE_DEFAULT : WellData.REGULAR_DEFAULT;
	}
	
	public static FluidStack getFillFluid(Biome biome, Level level, boolean upsideDown) {
		WellData data = getWellDataForBiome(biome, level, upsideDown);
		return data.fluid.copy();
	}
	
	public static int getFillDelay(Biome biome, Level level, RandomSource random, boolean upsideDown) {
		WellData data = getWellDataForBiome(biome, level, upsideDown);
		return data.maxToFill == data.minToFill ? data.maxToFill : random.nextInt(data.minToFill, data.maxToFill);
	}
	
	public static void configChanged(ModConfigEvent event) {
		if (event.getConfig().getType() == ModConfig.Type.SERVER) {
			WellData.REGULAR_DEFAULT.resetToDefault();
			WellData.UPSIDE_DEFAULT.resetToDefault();
			regularWellDataList.clear();
			upsideWellDataList.clear();
			
			dataList.get().forEach(WellConfig::initData);
		}
	}
	
}
