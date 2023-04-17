package cubicoder.well.data.common;

import java.util.function.Consumer;

import cubicoder.well.block.ModBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.common.Tags;

public class ModRecipeProvider extends RecipeProvider {

	public ModRecipeProvider(DataGenerator generator) {
		super(generator);
	}
	
	@Override
	protected void buildShapelessRecipes(Consumer<IFinishedRecipe> finishedRecipeConsumer) {
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
				.save(finishedRecipeConsumer);
		
		dyedWell(finishedRecipeConsumer, ModBlocks.WHITE_WELL.get(), Tags.Items.DYES_WHITE);
		dyedWell(finishedRecipeConsumer, ModBlocks.ORANGE_WELL.get(), Tags.Items.DYES_ORANGE);
		dyedWell(finishedRecipeConsumer, ModBlocks.MAGENTA_WELL.get(), Tags.Items.DYES_MAGENTA);
		dyedWell(finishedRecipeConsumer, ModBlocks.LIGHT_BLUE_WELL.get(), Tags.Items.DYES_LIGHT_BLUE);
		dyedWell(finishedRecipeConsumer, ModBlocks.YELLOW_WELL.get(), Tags.Items.DYES_YELLOW);
		dyedWell(finishedRecipeConsumer, ModBlocks.LIME_WELL.get(), Tags.Items.DYES_LIME);
		dyedWell(finishedRecipeConsumer, ModBlocks.PINK_WELL.get(), Tags.Items.DYES_PINK);
		dyedWell(finishedRecipeConsumer, ModBlocks.GRAY_WELL.get(), Tags.Items.DYES_GRAY);
		dyedWell(finishedRecipeConsumer, ModBlocks.LIGHT_GRAY_WELL.get(), Tags.Items.DYES_LIGHT_GRAY);
		dyedWell(finishedRecipeConsumer, ModBlocks.CYAN_WELL.get(), Tags.Items.DYES_CYAN);
		dyedWell(finishedRecipeConsumer, ModBlocks.PURPLE_WELL.get(), Tags.Items.DYES_PURPLE);
		dyedWell(finishedRecipeConsumer, ModBlocks.BLUE_WELL.get(), Tags.Items.DYES_BLUE);
		dyedWell(finishedRecipeConsumer, ModBlocks.BROWN_WELL.get(), Tags.Items.DYES_BROWN);
		dyedWell(finishedRecipeConsumer, ModBlocks.GREEN_WELL.get(), Tags.Items.DYES_GREEN);
		dyedWell(finishedRecipeConsumer, ModBlocks.RED_WELL.get(), Tags.Items.DYES_RED);
		dyedWell(finishedRecipeConsumer, ModBlocks.BLACK_WELL.get(), Tags.Items.DYES_BLACK);
	}
	
	private void dyedWell(Consumer<IFinishedRecipe> finishedRecipeConsumer, IItemProvider dyedWell, ITag<Item> dye) {
		ShapelessRecipeBuilder.shapeless(dyedWell).requires(dye).requires(ModBlocks.WELL.get()).group("wells")
				.unlockedBy("has_well", has(ModBlocks.WELL.get())).save(finishedRecipeConsumer);
	}
	
}
