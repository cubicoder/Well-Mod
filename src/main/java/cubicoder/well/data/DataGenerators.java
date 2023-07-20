package cubicoder.well.data;

import cubicoder.well.WellMod;
import cubicoder.well.data.client.ModBlockStateProvider;
import cubicoder.well.data.common.ModBlockTagsProvider;
import cubicoder.well.data.common.ModItemTagsProvider;
import cubicoder.well.data.common.ModLootTableProvider;
import cubicoder.well.data.common.ModRecipeProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = WellMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator gen = event.getGenerator();
		
		ModBlockTagsProvider blockTagGens = new ModBlockTagsProvider(gen, event.getExistingFileHelper());
		gen.addProvider(event.includeServer(), blockTagGens);
		gen.addProvider(event.includeServer(), new ModItemTagsProvider(gen, blockTagGens, event.getExistingFileHelper()));
		gen.addProvider(event.includeServer(), new ModRecipeProvider(gen));
		gen.addProvider(event.includeServer(), new ModLootTableProvider(gen));
		
		gen.addProvider(event.includeClient(), new ModBlockStateProvider(gen, event.getExistingFileHelper()));
		//gen.addProvider(event.includeClient(), new ModItemModelProvider(gen, event.getExistingFileHelper()));
	}
	
}
