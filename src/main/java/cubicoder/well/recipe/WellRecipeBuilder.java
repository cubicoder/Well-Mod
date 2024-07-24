package cubicoder.well.recipe;

import cubicoder.well.WellMod;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WellRecipeBuilder implements RecipeBuilder {
	
	private final List<ResourceLocation> biomes;
	private final List<TagKey<Biome>> biomeTags;
	private final int minTicks, maxTicks;
	private final FluidStack result;
	
	protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
	
	public WellRecipeBuilder(List<ResourceKey<Biome>> biomes, List<TagKey<Biome>> biomeTags, int minTicks, int maxTicks, FluidStack result) {
		this.biomes = biomes.stream().map(ResourceKey::location).toList();
		this.biomeTags = biomeTags;
		this.minTicks = minTicks;
		this.maxTicks = maxTicks;
		this.result = result;
	}
	
	@Override
	public RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
		this.criteria.put(name, criterion);
		return this;
	}
	
	@Override
	public RecipeBuilder group(@Nullable String groupName) {
		return this;
	}
	
	@Override
	public Item getResult() {
		return Items.AIR;
	}
	
	public Fluid getFluidResult() {
		return this.result.getFluid();
	}
	
	@Override
	public void save(RecipeOutput recipeOutput, ResourceLocation id) {
		Advancement.Builder advancement = recipeOutput.advancement()
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
				.rewards(AdvancementRewards.Builder.recipe(id))
				.requirements(AdvancementRequirements.Strategy.OR);
		this.criteria.forEach(advancement::addCriterion);
		WellRecipe recipe = new WellRecipe(this.biomes, this.biomeTags, this.minTicks, this.maxTicks, this.result);
		recipeOutput.accept(id, recipe, advancement.build(id.withPrefix("recipes/")));
	}
	
	@Override
	public void save(RecipeOutput recipeOutput) {
		this.save(recipeOutput, ResourceLocation.fromNamespaceAndPath(WellMod.MODID, BuiltInRegistries.FLUID.getKey(this.getFluidResult()).getPath()));
	}
	
}
