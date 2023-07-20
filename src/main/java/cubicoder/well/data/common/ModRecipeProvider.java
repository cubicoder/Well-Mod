package cubicoder.well.data.common;

import java.util.function.Consumer;

import cubicoder.well.block.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;

public class ModRecipeProvider extends RecipeProvider {

	public ModRecipeProvider(DataGenerator generator) {
		super(generator);
	}

	@Override
	protected void buildCraftingRecipes(Consumer<FinishedRecipe> writer) {		
		ShapedRecipeBuilder.shaped(ModBlocks.WELL.get())
				.pattern("RRR")
				.pattern("/L/")
				.pattern("SBS")
				.define('R', Tags.Items.INGOTS_BRICK)
				.define('/', Tags.Items.RODS_WOODEN)
				.define('L', Items.LEAD)
				.define('B', Items.BUCKET)
				.define('S', Blocks.STONE_BRICKS)
				.unlockedBy("has_bucket", has(Items.BUCKET))
				.save(writer);

		dyedWell(writer, ModBlocks.WHITE_WELL.get(), Tags.Items.DYES_WHITE);
		dyedWell(writer, ModBlocks.ORANGE_WELL.get(), Tags.Items.DYES_ORANGE);
		dyedWell(writer, ModBlocks.MAGENTA_WELL.get(), Tags.Items.DYES_MAGENTA);
		dyedWell(writer, ModBlocks.LIGHT_BLUE_WELL.get(), Tags.Items.DYES_LIGHT_BLUE);
		dyedWell(writer, ModBlocks.YELLOW_WELL.get(), Tags.Items.DYES_YELLOW);
		dyedWell(writer, ModBlocks.LIME_WELL.get(), Tags.Items.DYES_LIME);
		dyedWell(writer, ModBlocks.PINK_WELL.get(), Tags.Items.DYES_PINK);
		dyedWell(writer, ModBlocks.GRAY_WELL.get(), Tags.Items.DYES_GRAY);
		dyedWell(writer, ModBlocks.LIGHT_GRAY_WELL.get(), Tags.Items.DYES_LIGHT_GRAY);
		dyedWell(writer, ModBlocks.CYAN_WELL.get(), Tags.Items.DYES_CYAN);
		dyedWell(writer, ModBlocks.PURPLE_WELL.get(), Tags.Items.DYES_PURPLE);
		dyedWell(writer, ModBlocks.BLUE_WELL.get(), Tags.Items.DYES_BLUE);
		dyedWell(writer, ModBlocks.BROWN_WELL.get(), Tags.Items.DYES_BROWN);
		dyedWell(writer, ModBlocks.GREEN_WELL.get(), Tags.Items.DYES_GREEN);
		dyedWell(writer, ModBlocks.RED_WELL.get(), Tags.Items.DYES_RED);
		dyedWell(writer, ModBlocks.BLACK_WELL.get(), Tags.Items.DYES_BLACK);
	}
	
	private void dyedWell(Consumer<FinishedRecipe> writer, ItemLike dyedWell, TagKey<Item> dye) {
		ShapelessRecipeBuilder.shapeless(dyedWell).requires(dye).requires(ModBlocks.WELL.get()).group("wells")
				.unlockedBy("has_well", has(ModBlocks.WELL.get())).save(writer);
	}
	
}
