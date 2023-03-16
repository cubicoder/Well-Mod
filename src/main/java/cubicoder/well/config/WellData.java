package cubicoder.well.config;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

public class WellData {

	public static final WellData REGULAR_DEFAULT = new WellData(new FluidStack(Fluids.WATER, FluidAttributes.BUCKET_VOLUME), 160, 200);
	public static final WellData UPSIDE_DEFAULT = new WellData(new FluidStack(Fluids.WATER, FluidAttributes.BUCKET_VOLUME), 160, 200);
	
	public FluidStack fluid;
	public int minToFill;
	public int maxToFill;
	public List<ResourceLocation> biomes;
	public List<ResourceLocation> biomeTags;
	
	public WellData(FluidStack fluid, int minToFill, int maxToFill, List<ResourceLocation> biomes, List<ResourceLocation> biomeTags) {
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
	
	public boolean hasBiome(Biome biome, Level level) {
		for (ResourceLocation loc : biomes) {
			if (biome.getRegistryName().equals(loc)) return true;
		}
		
		Registry<Biome> reg = level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
		for (ResourceLocation tag : biomeTags) {
			for (Holder<Biome> b : reg.getOrCreateTag(TagKey.create(Registry.BIOME_REGISTRY, tag))) {
				if (biome.getRegistryName().equals(b.value().getRegistryName())) return true;
			}
		}
		
		return false;
	}
	
	public void resetToDefault() {
		fluid = new FluidStack(Fluids.WATER, FluidAttributes.BUCKET_VOLUME);
		minToFill = 160;
		maxToFill = 200;
	}
	
}
