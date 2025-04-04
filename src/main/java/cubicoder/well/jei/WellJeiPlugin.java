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

import javax.annotation.Nullable;
import java.util.List;

@JeiPlugin
public class WellJeiPlugin implements IModPlugin {
	
	@Nullable
	private WellRecipeCategory wellCategory;
	
	@Override
	public ResourceLocation getPluginUid() {
		return ResourceLocation.fromNamespaceAndPath(WellMod.MODID, "jei_plugin");
	}
	
	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		registration.addRecipeCategories(wellCategory = new WellRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
	}
	
	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		assert wellCategory != null;
		
		if (Minecraft.getInstance().level != null) {
			List<WellRecipe> wellRecipes = Minecraft.getInstance().level.getRecipeManager()
					.getAllRecipesFor(WellRecipeRegistration.WELL_RECIPE_TYPE.get())
					.stream().map(RecipeHolder::value).toList();
			registration.addRecipes(wellCategory.getRecipeType(), wellRecipes);
		}
	}
	
	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		assert wellCategory != null;
		
		registration.addRecipeCatalyst(new ItemStack(ModItems.WELL.get()), wellCategory.getRecipeType());
		registration.addRecipeCatalyst(new ItemStack(ModItems.WHITE_WELL.get()), wellCategory.getRecipeType());
		registration.addRecipeCatalyst(new ItemStack(ModItems.LIGHT_GRAY_WELL.get()), wellCategory.getRecipeType());
		registration.addRecipeCatalyst(new ItemStack(ModItems.GRAY_WELL.get()), wellCategory.getRecipeType());
		registration.addRecipeCatalyst(new ItemStack(ModItems.BLACK_WELL.get()), wellCategory.getRecipeType());
		registration.addRecipeCatalyst(new ItemStack(ModItems.BROWN_WELL.get()), wellCategory.getRecipeType());
		registration.addRecipeCatalyst(new ItemStack(ModItems.RED_WELL.get()), wellCategory.getRecipeType());
		registration.addRecipeCatalyst(new ItemStack(ModItems.ORANGE_WELL.get()), wellCategory.getRecipeType());
		registration.addRecipeCatalyst(new ItemStack(ModItems.YELLOW_WELL.get()), wellCategory.getRecipeType());
		registration.addRecipeCatalyst(new ItemStack(ModItems.LIME_WELL.get()), wellCategory.getRecipeType());
		registration.addRecipeCatalyst(new ItemStack(ModItems.GREEN_WELL.get()), wellCategory.getRecipeType());
		registration.addRecipeCatalyst(new ItemStack(ModItems.CYAN_WELL.get()), wellCategory.getRecipeType());
		registration.addRecipeCatalyst(new ItemStack(ModItems.LIGHT_BLUE_WELL.get()), wellCategory.getRecipeType());
		registration.addRecipeCatalyst(new ItemStack(ModItems.BLUE_WELL.get()), wellCategory.getRecipeType());
		registration.addRecipeCatalyst(new ItemStack(ModItems.PURPLE_WELL.get()), wellCategory.getRecipeType());
		registration.addRecipeCatalyst(new ItemStack(ModItems.MAGENTA_WELL.get()), wellCategory.getRecipeType());
		registration.addRecipeCatalyst(new ItemStack(ModItems.PINK_WELL.get()), wellCategory.getRecipeType());
	}
	
}
