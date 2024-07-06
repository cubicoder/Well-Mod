package cubicoder.well.data.common;

import cubicoder.well.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

public class ModRecipeProvider extends RecipeProvider {

	public ModRecipeProvider(PackOutput output) {
		super(output);
	}

	@Override
	protected void buildRecipes(RecipeOutput recipeOutput) {
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.WELL.get())
				.pattern("RRR")
				.pattern("/L/")
				.pattern("SBS")
				.define('R', Tags.Items.INGOTS_BRICK)
				.define('/', Tags.Items.RODS_WOODEN)
				.define('L', Items.LEAD)
				.define('B', Items.BUCKET)
				.define('S', Blocks.STONE_BRICKS)
				.unlockedBy("has_bucket", has(Items.BUCKET))
				.save(recipeOutput);

		dyedWell(recipeOutput, ModBlocks.WHITE_WELL.get(), Tags.Items.DYES_WHITE);
		dyedWell(recipeOutput, ModBlocks.ORANGE_WELL.get(), Tags.Items.DYES_ORANGE);
		dyedWell(recipeOutput, ModBlocks.MAGENTA_WELL.get(), Tags.Items.DYES_MAGENTA);
		dyedWell(recipeOutput, ModBlocks.LIGHT_BLUE_WELL.get(), Tags.Items.DYES_LIGHT_BLUE);
		dyedWell(recipeOutput, ModBlocks.YELLOW_WELL.get(), Tags.Items.DYES_YELLOW);
		dyedWell(recipeOutput, ModBlocks.LIME_WELL.get(), Tags.Items.DYES_LIME);
		dyedWell(recipeOutput, ModBlocks.PINK_WELL.get(), Tags.Items.DYES_PINK);
		dyedWell(recipeOutput, ModBlocks.GRAY_WELL.get(), Tags.Items.DYES_GRAY);
		dyedWell(recipeOutput, ModBlocks.LIGHT_GRAY_WELL.get(), Tags.Items.DYES_LIGHT_GRAY);
		dyedWell(recipeOutput, ModBlocks.CYAN_WELL.get(), Tags.Items.DYES_CYAN);
		dyedWell(recipeOutput, ModBlocks.PURPLE_WELL.get(), Tags.Items.DYES_PURPLE);
		dyedWell(recipeOutput, ModBlocks.BLUE_WELL.get(), Tags.Items.DYES_BLUE);
		dyedWell(recipeOutput, ModBlocks.BROWN_WELL.get(), Tags.Items.DYES_BROWN);
		dyedWell(recipeOutput, ModBlocks.GREEN_WELL.get(), Tags.Items.DYES_GREEN);
		dyedWell(recipeOutput, ModBlocks.RED_WELL.get(), Tags.Items.DYES_RED);
		dyedWell(recipeOutput, ModBlocks.BLACK_WELL.get(), Tags.Items.DYES_BLACK);
	}
	
	private void dyedWell(RecipeOutput recipeOutput, ItemLike dyedWell, TagKey<Item> dye) {
		ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, dyedWell).requires(dye)
				.requires(ModBlocks.WELL.get()).group("wells").unlockedBy("has_well", has(ModBlocks.WELL.get()))
				.save(recipeOutput);
	}
	
}
