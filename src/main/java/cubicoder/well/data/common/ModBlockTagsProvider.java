package cubicoder.well.data.common;

import cubicoder.well.WellMod;
import cubicoder.well.block.ModBlocks;
import cubicoder.well.tags.ModBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {

	public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
			@Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, WellMod.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
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
		
		tag(BlockTags.MINEABLE_WITH_PICKAXE).addTag(ModBlockTags.WELLS);

		tag(Tags.Blocks.DYED_WHITE).add(ModBlocks.WHITE_WELL.get());
		tag(Tags.Blocks.DYED_ORANGE).add(ModBlocks.ORANGE_WELL.get());
		tag(Tags.Blocks.DYED_MAGENTA).add(ModBlocks.MAGENTA_WELL.get());
		tag(Tags.Blocks.DYED_LIGHT_BLUE).add(ModBlocks.LIGHT_BLUE_WELL.get());
		tag(Tags.Blocks.DYED_YELLOW).add(ModBlocks.YELLOW_WELL.get());
		tag(Tags.Blocks.DYED_LIME).add(ModBlocks.LIME_WELL.get());
		tag(Tags.Blocks.DYED_PINK).add(ModBlocks.PINK_WELL.get());
		tag(Tags.Blocks.DYED_GRAY).add(ModBlocks.GRAY_WELL.get());
		tag(Tags.Blocks.DYED_LIGHT_GRAY).add(ModBlocks.LIGHT_GRAY_WELL.get());
		tag(Tags.Blocks.DYED_CYAN).add(ModBlocks.CYAN_WELL.get());
		tag(Tags.Blocks.DYED_PURPLE).add(ModBlocks.PURPLE_WELL.get());
		tag(Tags.Blocks.DYED_BLUE).add(ModBlocks.BLUE_WELL.get());
		tag(Tags.Blocks.DYED_BROWN).add(ModBlocks.BROWN_WELL.get());
		tag(Tags.Blocks.DYED_GREEN).add(ModBlocks.GREEN_WELL.get());
		tag(Tags.Blocks.DYED_RED).add(ModBlocks.RED_WELL.get());
		tag(Tags.Blocks.DYED_BLACK).add(ModBlocks.BLACK_WELL.get());
	}
	
	@Override
	public String getName() {
		return WellMod.MOD_NAME + " Block Tags";
	}
	
}
