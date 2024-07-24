package cubicoder.well.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public record WellRecipe(List<ResourceLocation> biomes, List<TagKey<Biome>> biomeTags, int minTicks, int maxTicks, FluidStack result) implements Recipe<WellRecipeInput> {
	
	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}
	
	@Override
	public boolean matches(WellRecipeInput input, Level level) {
		Registry<Biome> reg = level.registryAccess().registryOrThrow(Registries.BIOME);
		ResourceLocation biomeName = reg.getKey(input.biome().value());
		
		for (ResourceLocation biome : biomes) {
			if (biome.equals(biomeName)) return true;
		}
		
		for (TagKey<Biome> biomeTag : biomeTags) {
			if (input.biome().is(biomeTag)) return true;
		}
		
		return false;
	}
	
	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return ItemStack.EMPTY;
	}
	
	public FluidStack getResultFluid(HolderLookup.Provider ignoredRegistries) {
		return result;
	}
	
	@Override
	public ItemStack assemble(WellRecipeInput input, HolderLookup.Provider registries) {
		return ItemStack.EMPTY.copy();
	}
	
	public FluidStack assembleFluid(WellRecipeInput ignoredInput, HolderLookup.Provider ignoredRegistries) {
		return result.copy();
	}
	
	@Override
	public RecipeType<?> getType() {
		return WellRecipeRegistration.WELL_RECIPE_TYPE.get();
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return WellRecipeRegistration.WELL_RECIPE_SERIALIZER.get();
	}
	
	
}
