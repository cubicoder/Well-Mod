package cubicoder.well.data.common;

import javax.annotation.Nullable;

import cubicoder.well.WellMod;
import cubicoder.well.tags.ModBlockTags;
import cubicoder.well.tags.ModItemTags;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemTagsProvider extends ItemTagsProvider {

	public ModItemTagsProvider(DataGenerator generator, BlockTagsProvider blockTagsProvider, 
			@Nullable ExistingFileHelper existingFileHelper) {
		super(generator, blockTagsProvider, WellMod.MODID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		copy(ModBlockTags.WELLS, ModItemTags.WELLS);
	}
	
	@Override
	public String getName() {
		return WellMod.MOD_NAME + " Item Tags";
	}
	
}
