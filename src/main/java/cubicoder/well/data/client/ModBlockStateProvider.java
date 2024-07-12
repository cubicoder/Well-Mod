package cubicoder.well.data.client;

import cubicoder.well.WellMod;
import cubicoder.well.block.ModBlocks;
import cubicoder.well.block.WellBlock;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {

	private final ModelFile.ExistingModelFile wellBase;

	public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
		super(output, WellMod.MODID, exFileHelper);
		wellBase = new ModelFile.ExistingModelFile(modLoc(BlockModelProvider.BLOCK_FOLDER + "/well_base"), exFileHelper);
	}
	
	@Override
	protected void registerStatesAndModels() {
		ModBlocks.BLOCKS.getEntries().forEach(block -> {
			wellBlockRoof(block.get());
			wellItemModel(block.get());
			wellBlockState(block.get());
		});
	}
	
	private void wellBlockRoof(Block block) {
		String path = BuiltInRegistries.BLOCK.getKey(block).getPath();
		String color = path.length() > 4 ? path.substring(0, path.length() - 5) : "brick";
		this.models().withExistingParent(path + "_roof", modLoc(BlockModelProvider.BLOCK_FOLDER + "/template_well_roof"))
				.texture("roof", modLoc(BlockModelProvider.BLOCK_FOLDER + "/" + color + "_roof"));
	}
	
	private void wellItemModel(Block block) {
		String path = BuiltInRegistries.BLOCK.getKey(block).getPath();
		String color = path.length() > 4 ? path.substring(0, path.length() - 5) : "brick";
		this.itemModels().withExistingParent(path, modLoc(ItemModelProvider.BLOCK_FOLDER + "/template_well"))
				.texture("roof", modLoc(ItemModelProvider.BLOCK_FOLDER + "/" + color + "_roof"));
	}
	
	private void wellBlockState(Block block) {
		String path = BuiltInRegistries.BLOCK.getKey(block).getPath();
		String color = path.length() > 4 ? path.substring(0, path.length() - 5) + "_" : "";
		ModelFile.UncheckedModelFile wellRoof = new ModelFile.UncheckedModelFile(modLoc(BlockModelProvider.BLOCK_FOLDER + "/" + color + "well_roof"));
		this.getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder()
				.modelFile(state.getValue(WellBlock.HALF).equals(DoubleBlockHalf.LOWER) ? wellBase : wellRoof)
				.rotationY(state.getValue(WellBlock.AXIS).equals(Axis.X) ? 0 : 90)
				.rotationX(state.getValue(WellBlock.UPSIDE_DOWN) ? 180 : 0).build());
	}
	
}
