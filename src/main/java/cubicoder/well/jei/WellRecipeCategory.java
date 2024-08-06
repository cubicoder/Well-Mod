package cubicoder.well.jei;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import cubicoder.well.WellMod;
import cubicoder.well.item.ModItems;
import cubicoder.well.recipe.WellRecipe;
import cubicoder.well.recipe.WellRecipeRegistration;
import mezz.jei.api.constants.ModIds;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class WellRecipeCategory implements IRecipeCategory<WellRecipe> {
	
	public static final RecipeType<WellRecipe> RECIPE_TYPE = RecipeType.create(WellMod.MODID, WellRecipeRegistration.RECIPE_NAME, WellRecipe.class);
	
	private static final ResourceLocation RECIPE_GUI_VANILLA = ResourceLocation.fromNamespaceAndPath(ModIds.JEI_ID, "textures/jei/gui/gui_vanilla.png");
	
	private final IDrawable background;
	private final IDrawable icon;
	private final LoadingCache<Integer, IDrawableAnimated> cachedArrows;
	
	public WellRecipeCategory(IGuiHelper guiHelper) {
		background = guiHelper.createDrawable(RECIPE_GUI_VANILLA, 0 ,224, 82, 26);
		icon = guiHelper.createDrawableItemStack(new ItemStack(ModItems.WELL.get()));
		cachedArrows = CacheBuilder.newBuilder().build(
				new CacheLoader<>() {
					@Override
					public IDrawableAnimated load(Integer time) {
						return guiHelper.drawableBuilder(RECIPE_GUI_VANILLA, 82, 128, 24, 17).buildAnimated(time, IDrawableAnimated.StartDirection.LEFT, false);
					}
				}
		);
	}
	
	@Override
	public RecipeType<WellRecipe> getRecipeType() {
		return RECIPE_TYPE;
	}
	
	@Override
	public Component getTitle() {
		return Component.translatable("gui.well.category.well");
	}
	
	@Override
	public IDrawable getBackground() {
		return background;
	}
	
	@Override
	public @Nullable IDrawable getIcon() {
		return icon;
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
		
		builder.addSlot(RecipeIngredientRole.CATALYST, 1, 5).addItemStacks(wells)
				.addTooltipCallback(((recipeSlotView, tooltip) -> {
					tooltip.add(Component.translatable("well.tooltip.generation"));
					recipe.biomes().forEach(resLoc -> tooltip.add(Component.translatable(Util.makeDescriptionId("biome", resLoc))));
					recipe.biomeTags().forEach(biomeTag -> tooltip.add(Component.literal(biomeTag.location().toString())));
				}));
		builder.addSlot(RecipeIngredientRole.OUTPUT, 61, 5).addFluidStack(recipe.result().getFluid(), recipe.result().getAmount())
				.addTooltipCallback(((recipeSlotView, tooltip) -> tooltip.add(Component.translatable("jei.tooltip.liquid.amount", recipe.result().getAmount()))));
	}
	
	@Override
	public void draw(WellRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
		cachedArrows.getUnchecked(recipe.maxTicks()).draw(guiGraphics, 24, 4);
	}
	
	@Override
	public List<Component> getTooltipStrings(WellRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
		List<Component> tooltipStrings = new ArrayList<>();
		
		// if mouse is over arrow
		if (mouseX >= 25 && mouseY >= 5 && mouseX <= 46 && mouseY <= 19) {
			if (recipe.minTicks() == recipe.maxTicks()) {
				tooltipStrings.add(Component.translatable("well.tooltip.time", recipe.maxTicks()));
			} else {
				tooltipStrings.add(Component.translatable("well.tooltip.time.range", recipe.minTicks(), recipe.maxTicks()));
			}
		}
		
		return tooltipStrings;
	}
	
}
