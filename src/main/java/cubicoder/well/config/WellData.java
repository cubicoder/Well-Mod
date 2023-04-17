package cubicoder.well.config;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.fluid.Fluids;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

public class WellData {

	public static final WellData REGULAR_DEFAULT = new WellData(new FluidStack(Fluids.WATER, FluidAttributes.BUCKET_VOLUME), 160, 200);
	public static final WellData UPSIDE_DEFAULT = new WellData(new FluidStack(Fluids.WATER, FluidAttributes.BUCKET_VOLUME), 160, 200);
	
	public FluidStack fluid;
	public int minToFill;
	public int maxToFill;
	public List<Biome> biomes;
	public List<BiomeDictionary.Type> biomeTags;
	
	public WellData(FluidStack fluid, int minToFill, int maxToFill, List<Biome> biomes, List<BiomeDictionary.Type> biomeTags) {
		this.fluid = fluid;
		this.minToFill = minToFill;
		this.maxToFill = maxToFill;
		this.biomes = biomes;
		this.biomeTags = biomeTags;
	}
	
	public WellData(FluidStack fluid, int minToFill, int maxToFill) {
		this.fluid = fluid;
		this.minToFill = minToFill;
		this.maxToFill = maxToFill;
		this.biomes = new ArrayList<>();
		this.biomeTags = new ArrayList<>();
	}
	
	public boolean hasBiome(Biome biome, World level) {
		for (Biome b : biomes) {
			if (biome.getRegistryName().equals(b.getRegistryName())) return true;
		}
		
		RegistryKey<Biome> biomeKey = RegistryKey.create(Registry.BIOME_REGISTRY, biome.getRegistryName());
		for (BiomeDictionary.Type c : biomeTags) {
			if (BiomeDictionary.getBiomes(c).contains(biomeKey)) return true;
		}
		
		return false;
	}
	
	public void resetToDefault() {
		fluid = new FluidStack(Fluids.WATER, FluidAttributes.BUCKET_VOLUME);
		minToFill = 160;
		maxToFill = 200;
	}
	
}
