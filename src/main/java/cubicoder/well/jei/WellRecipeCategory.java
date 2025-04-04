package cubicoder.well.jei;

import com.mojang.serialization.Codec;
import cubicoder.well.WellMod;
import cubicoder.well.item.ModItems;
import cubicoder.well.recipe.WellRecipe;
import cubicoder.well.recipe.WellRecipeRegistration;
import cubicoder.well.recipe.WellRecipeSerializer;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.ICodecHelper;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WellRecipeCategory extends AbstractRecipeCategory<WellRecipe> {
	
	public WellRecipeCategory(IGuiHelper guiHelper) {
		super(
				RecipeType.create(WellMod.MODID, WellRecipeRegistration.RECIPE_NAME, WellRecipe.class),
				Component.translatable("gui.well.category.well"),
				guiHelper.createDrawableItemLike(ModItems.WELL.get()),
				82,
				34
		);
	}
	
	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, WellRecipe recipe, IFocusGroup focuses) {
		List<ItemStack> wells = List.of(
				new ItemStack(ModItems.WELL.get()),
				new ItemStack(ModItems.WHITE_WELL.get()),
				new ItemStack(ModItems.LIGHT_GRAY_WELL.get()),
				new ItemStack(ModItems.GRAY_WELL.get()),
				new ItemStack(ModItems.BLACK_WELL.get()),
				new ItemStack(ModItems.BROWN_WELL.get()),
				new ItemStack(ModItems.RED_WELL.get()),
				new ItemStack(ModItems.ORANGE_WELL.get()),
				new ItemStack(ModItems.YELLOW_WELL.get()),
				new ItemStack(ModItems.LIME_WELL.get()),
				new ItemStack(ModItems.GREEN_WELL.get()),
				new ItemStack(ModItems.CYAN_WELL.get()),
				new ItemStack(ModItems.LIGHT_BLUE_WELL.get()),
				new ItemStack(ModItems.BLUE_WELL.get()),
				new ItemStack(ModItems.PURPLE_WELL.get()),
				new ItemStack(ModItems.MAGENTA_WELL.get()),
				new ItemStack(ModItems.PINK_WELL.get())
		);
		
		builder.addSlot(RecipeIngredientRole.CATALYST, 1, 9)
				.setStandardSlotBackground()
				.addItemStacks(wells);
		builder.addOutputSlot(61, 9)
				.setOutputSlotBackground()
				.addFluidStack(recipe.result().getFluid(), recipe.result().getAmount());
	}
	
	@Override
	public void createRecipeExtras(IRecipeExtrasBuilder builder, WellRecipe recipe, IFocusGroup focuses) {
		builder.addAnimatedRecipeArrow(recipe.maxTicks()).setPosition(26, 9);
	}
	
	@Override
	public void getTooltip(ITooltipBuilder tooltip, WellRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
		// if mouse is over arrow
		if (mouseX >= 26 && mouseY >= 9 && mouseX <= 47 && mouseY <= 24) {
			// amount
			tooltip.add(Component.translatable("jei.tooltip.liquid.amount", recipe.result().getAmount()));
			
			// time
			if (recipe.minTicks() == recipe.maxTicks()) {
				tooltip.add(Component.translatable("well.tooltip.time", recipe.maxTicks()));
			} else {
				tooltip.add(Component.translatable("well.tooltip.time.range", recipe.minTicks(), recipe.maxTicks()));
			}
			
			// location
			tooltip.add(Component.translatable("well.tooltip.generation"));
			recipe.biomes().forEach(resLoc -> tooltip.add(Component.translatable(Util.makeDescriptionId("biome", resLoc))));
			recipe.biomeTags().forEach(biomeTag -> tooltip.add(Component.literal(biomeTag.location().toString())));
		}
	}
	
	@Override
	public @Nullable ResourceLocation getRegistryName(WellRecipe recipe) {
		return ResourceLocation.fromNamespaceAndPath(WellMod.MODID, BuiltInRegistries.FLUID.getKey(recipe.result().getFluid()).getPath());
	}
	
	@Override
	public Codec<WellRecipe> getCodec(ICodecHelper codecHelper, IRecipeManager recipeManager) {
		return WellRecipeSerializer.CODEC.codec();
	}
	
}
