package cubicoder.well.data.client;

import cubicoder.well.WellMod;
import cubicoder.well.block.ModBlocks;
import cubicoder.well.block.WellBlock;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.Direction.Axis;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile.ExistingModelFile;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {

	private ExistingModelFile wellBase;
	
	public ModBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
		super(gen, WellMod.MODID, exFileHelper);
		wellBase = new ExistingModelFile(modLoc(BlockModelProvider.BLOCK_FOLDER + "/well_base"), exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		ModBlocks.BLOCKS.getEntries().forEach(block -> {
			wellBlockRoof(block.get());
			wellItemModel(block.get());
			wellBlockState(block.get());
		});
	}
	
	private BlockModelBuilder wellBlockRoof(Block block) {
		String path = block.getRegistryName().getPath();
		String color = path.length() > 4 ? path.substring(0, path.length() - 5) : "brick";
		return this.models().withExistingParent(path + "_roof", modLoc(BlockModelProvider.BLOCK_FOLDER + "/template_well_roof"))
				.texture("roof", modLoc(BlockModelProvider.BLOCK_FOLDER + "/" + color + "_roof"));
	}
	
	private ItemModelBuilder wellItemModel(Block block) {
		String path = block.getRegistryName().getPath();
		String color = path.length() > 4 ? path.substring(0, path.length() - 5) : "brick";
		return this.itemModels().withExistingParent(path, modLoc(ItemModelProvider.BLOCK_FOLDER + "/template_well"))
				.texture("roof", modLoc(ItemModelProvider.BLOCK_FOLDER + "/" + color + "_roof"));
	}
	
	private VariantBlockStateBuilder wellBlockState(Block block) {
		String path = block.getRegistryName().getPath();
		String color = path.length() > 4 ? path.substring(0, path.length() - 5) + "_" : "";
		UncheckedModelFile wellRoof = new UncheckedModelFile(modLoc(BlockModelProvider.BLOCK_FOLDER + "/" + color + "well_roof"));
		return this.getVariantBuilder(block)
				.forAllStates(state -> ConfiguredModel.builder()
						.modelFile(state.getValue(WellBlock.HALF).equals(DoubleBlockHalf.LOWER) ? wellBase : wellRoof)
						.rotationY(state.getValue(WellBlock.AXIS).equals(Axis.X) ? 0 : 90)
						.rotationX(state.getValue(WellBlock.UPSIDE_DOWN) ? 180 : 0).build());
	}
	
}
