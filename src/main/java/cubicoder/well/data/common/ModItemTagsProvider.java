package cubicoder.well.data.common;

import cubicoder.well.WellMod;
import cubicoder.well.item.ModItems;
import cubicoder.well.tags.ModBlockTags;
import cubicoder.well.tags.ModItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider {

	public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
			CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, blockTags, WellMod.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		copy(ModBlockTags.WELLS, ModItemTags.WELLS);

		tag(Tags.Items.DYED_WHITE).add(ModItems.WHITE_WELL.get());
		tag(Tags.Items.DYED_ORANGE).add(ModItems.ORANGE_WELL.get());
		tag(Tags.Items.DYED_MAGENTA).add(ModItems.MAGENTA_WELL.get());
		tag(Tags.Items.DYED_LIGHT_BLUE).add(ModItems.LIGHT_BLUE_WELL.get());
		tag(Tags.Items.DYED_YELLOW).add(ModItems.YELLOW_WELL.get());
		tag(Tags.Items.DYED_LIME).add(ModItems.LIME_WELL.get());
		tag(Tags.Items.DYED_PINK).add(ModItems.PINK_WELL.get());
		tag(Tags.Items.DYED_GRAY).add(ModItems.GRAY_WELL.get());
		tag(Tags.Items.DYED_LIGHT_GRAY).add(ModItems.LIGHT_GRAY_WELL.get());
		tag(Tags.Items.DYED_CYAN).add(ModItems.CYAN_WELL.get());
		tag(Tags.Items.DYED_PURPLE).add(ModItems.PURPLE_WELL.get());
		tag(Tags.Items.DYED_BLUE).add(ModItems.BLUE_WELL.get());
		tag(Tags.Items.DYED_BROWN).add(ModItems.BROWN_WELL.get());
		tag(Tags.Items.DYED_GREEN).add(ModItems.GREEN_WELL.get());
		tag(Tags.Items.DYED_RED).add(ModItems.RED_WELL.get());
		tag(Tags.Items.DYED_BLACK).add(ModItems.BLACK_WELL.get());
	}
	
	@Override
	public String getName() {
		return WellMod.MOD_NAME + " Item Tags";
	}
	
}
