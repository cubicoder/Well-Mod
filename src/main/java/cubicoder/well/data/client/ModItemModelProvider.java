package cubicoder.well.data.client;

import cubicoder.well.WellMod;
import cubicoder.well.block.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItemModelProvider extends ItemModelProvider {

	public ModItemModelProvider(DataGenerator gen, ExistingFileHelper existingFileHelper) {
		super(gen, WellMod.MODID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		wellBlockItem(ModBlocks.WELL.get());
		wellBlockItem(ModBlocks.WHITE_WELL.get());
		wellBlockItem(ModBlocks.ORANGE_WELL.get());
		wellBlockItem(ModBlocks.MAGENTA_WELL.get());
		wellBlockItem(ModBlocks.LIGHT_BLUE_WELL.get());
		wellBlockItem(ModBlocks.YELLOW_WELL.get());
		wellBlockItem(ModBlocks.LIME_WELL.get());
		wellBlockItem(ModBlocks.PINK_WELL.get());
		wellBlockItem(ModBlocks.GRAY_WELL.get());
		wellBlockItem(ModBlocks.LIGHT_GRAY_WELL.get());
		wellBlockItem(ModBlocks.CYAN_WELL.get());
		wellBlockItem(ModBlocks.PURPLE_WELL.get());
		wellBlockItem(ModBlocks.BLUE_WELL.get());
		wellBlockItem(ModBlocks.BROWN_WELL.get());
		wellBlockItem(ModBlocks.GREEN_WELL.get());
		wellBlockItem(ModBlocks.RED_WELL.get());
		wellBlockItem(ModBlocks.BLACK_WELL.get());
	}

	private ItemModelBuilder wellBlockItem(Block block) {
		String path = ForgeRegistries.BLOCKS.getKey(block).getPath();
		String color = path.length() > 4 ? path.substring(0, path.length() - 5) : "brick";
		return withExistingParent(path, modLoc(BLOCK_FOLDER + "/well")).texture("roof", modLoc(BLOCK_FOLDER + "/" + color + "_roof"));
	}
	
}
