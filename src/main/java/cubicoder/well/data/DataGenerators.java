package cubicoder.well.data;

import cubicoder.well.data.client.ModBlockStateProvider;
import cubicoder.well.data.client.ModSpriteSourceProvider;
import cubicoder.well.data.common.ModBlockLootSubProvider;
import cubicoder.well.data.common.ModBlockTagsProvider;
import cubicoder.well.data.common.ModItemTagsProvider;
import cubicoder.well.data.common.ModRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DataGenerators {

	public static void gatherData(GatherDataEvent event) {
		DataGenerator gen = event.getGenerator();
		PackOutput output = gen.getPackOutput();
		ExistingFileHelper fileHelper = event.getExistingFileHelper();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
		
		ModBlockTagsProvider blockTagGens = new ModBlockTagsProvider(output, lookupProvider, fileHelper);
		gen.addProvider(event.includeServer(), blockTagGens);
		gen.addProvider(event.includeServer(), new ModItemTagsProvider(output, lookupProvider, blockTagGens.contentsGetter(), fileHelper));
		gen.addProvider(event.includeServer(), new ModRecipeProvider(output, lookupProvider));
		gen.addProvider(event.includeServer(), new LootTableProvider(output, Collections.emptySet(),
				List.of(new LootTableProvider.SubProviderEntry(ModBlockLootSubProvider::new, LootContextParamSets.BLOCK)), lookupProvider));

		gen.addProvider(event.includeClient(), new ModBlockStateProvider(output, fileHelper));
		gen.addProvider(event.includeClient(), new ModSpriteSourceProvider(output, lookupProvider, fileHelper));
	}
	
}
