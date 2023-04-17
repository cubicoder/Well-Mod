package cubicoder.well.data.common;

import javax.annotation.Nullable;

import cubicoder.well.WellMod;
import cubicoder.well.block.ModBlocks;
import cubicoder.well.tags.ModBlockTags;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockTagsProvider extends BlockTagsProvider {

	public ModBlockTagsProvider(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
		super(generator, WellMod.MODID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		tag(ModBlockTags.WELLS).add(
				ModBlocks.WELL.get(),
				ModBlocks.WHITE_WELL.get(),
				ModBlocks.ORANGE_WELL.get(),
				ModBlocks.MAGENTA_WELL.get(),
				ModBlocks.LIGHT_BLUE_WELL.get(),
				ModBlocks.YELLOW_WELL.get(),
				ModBlocks.LIME_WELL.get(),
				ModBlocks.PINK_WELL.get(),
				ModBlocks.GRAY_WELL.get(),
				ModBlocks.LIGHT_GRAY_WELL.get(),
				ModBlocks.CYAN_WELL.get(),
				ModBlocks.PURPLE_WELL.get(),
				ModBlocks.BLUE_WELL.get(),
				ModBlocks.BROWN_WELL.get(),
				ModBlocks.GREEN_WELL.get(),
				ModBlocks.RED_WELL.get(),
				ModBlocks.BLACK_WELL.get());
	}
	
	@Override
	public String getName() {
		return WellMod.MOD_NAME + " Block Tags";
	}
	
}
