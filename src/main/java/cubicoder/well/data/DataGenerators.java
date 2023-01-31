package cubicoder.well.data;

import cubicoder.well.WellMod;
import cubicoder.well.data.client.ModBlockStateProvider;
import cubicoder.well.data.common.ModBlockTagsProvider;
import cubicoder.well.data.common.ModItemTagsProvider;
import cubicoder.well.data.common.ModLootTableProvider;
import cubicoder.well.data.common.ModRecipeProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@EventBusSubscriber(modid = WellMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator gen = event.getGenerator();
		
		if (event.includeServer()) {
			ModBlockTagsProvider blockTagGens = new ModBlockTagsProvider(gen, event.getExistingFileHelper());
			gen.addProvider(new ModRecipeProvider(gen));
			gen.addProvider(new ModLootTableProvider(gen));
			gen.addProvider(blockTagGens);
			gen.addProvider(new ModItemTagsProvider(gen, blockTagGens, event.getExistingFileHelper()));
		}
		if (event.includeClient()) {
			gen.addProvider(new ModBlockStateProvider(gen, event.getExistingFileHelper()));
			//gen.addProvider(new ModItemModelProvider(gen, event.getExistingFileHelper()));
			//gen.addProvider(new ModLanguageProvider(gen, "en_us"));
		}
	}
	
}
