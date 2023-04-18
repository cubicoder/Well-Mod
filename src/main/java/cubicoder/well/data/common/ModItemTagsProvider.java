package cubicoder.well.data.common;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import cubicoder.well.WellMod;
import cubicoder.well.tags.ModBlockTags;
import cubicoder.well.tags.ModItemTags;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemTagsProvider extends ItemTagsProvider {

	public ModItemTagsProvider(PackOutput output, CompletableFuture<Provider> lookupProvider,
			CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, blockTags, WellMod.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(Provider provider) {
		copy(ModBlockTags.WELLS, ModItemTags.WELLS);
	}
	
	@Override
	public String getName() {
		return WellMod.MOD_NAME + " Item Tags";
	}
	
}
