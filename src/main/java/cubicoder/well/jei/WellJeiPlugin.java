package cubicoder.well.jei;

import cubicoder.well.WellMod;
import cubicoder.well.item.ModItems;
import cubicoder.well.recipe.WellRecipe;
import cubicoder.well.recipe.WellRecipeRegistration;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

@JeiPlugin
public class WellJeiPlugin implements IModPlugin {
	
	@Override
	public ResourceLocation getPluginUid() {
		return ResourceLocation.fromNamespaceAndPath(WellMod.MODID, "jei_plugin");
	}
	
	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		registration.addRecipeCategories(new WellRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
	}
	
	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		if (Minecraft.getInstance().level != null) {
			List<WellRecipe> wellRecipes = Minecraft.getInstance().level.getRecipeManager()
					.getAllRecipesFor(WellRecipeRegistration.WELL_RECIPE_TYPE.get())
					.stream().map(RecipeHolder::value).toList();
			registration.addRecipes(WellRecipeCategory.RECIPE_TYPE, wellRecipes);
		}
	}
	
	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(ModItems.WELL.get()), WellRecipeCategory.RECIPE_TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModItems.WHITE_WELL.get()), WellRecipeCategory.RECIPE_TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModItems.LIGHT_GRAY_WELL.get()), WellRecipeCategory.RECIPE_TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModItems.GRAY_WELL.get()), WellRecipeCategory.RECIPE_TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModItems.BLACK_WELL.get()), WellRecipeCategory.RECIPE_TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModItems.BROWN_WELL.get()), WellRecipeCategory.RECIPE_TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModItems.RED_WELL.get()), WellRecipeCategory.RECIPE_TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModItems.ORANGE_WELL.get()), WellRecipeCategory.RECIPE_TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModItems.YELLOW_WELL.get()), WellRecipeCategory.RECIPE_TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModItems.LIME_WELL.get()), WellRecipeCategory.RECIPE_TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModItems.GREEN_WELL.get()), WellRecipeCategory.RECIPE_TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModItems.CYAN_WELL.get()), WellRecipeCategory.RECIPE_TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModItems.LIGHT_BLUE_WELL.get()), WellRecipeCategory.RECIPE_TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModItems.BLUE_WELL.get()), WellRecipeCategory.RECIPE_TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModItems.PURPLE_WELL.get()), WellRecipeCategory.RECIPE_TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModItems.MAGENTA_WELL.get()), WellRecipeCategory.RECIPE_TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModItems.PINK_WELL.get()), WellRecipeCategory.RECIPE_TYPE);
	}
	
}
