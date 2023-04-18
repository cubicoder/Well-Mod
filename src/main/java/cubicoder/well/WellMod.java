package cubicoder.well;

import cubicoder.well.block.ModBlocks;
import cubicoder.well.client.WellRenderer;
import cubicoder.well.config.WellConfig;
import cubicoder.well.item.ModItems;
import cubicoder.well.sound.ModSounds;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(WellMod.MODID)
public final class WellMod {
	
	public static final String MODID = "well";
	public static final String MOD_NAME = "Well Mod";

	public WellMod() {
		ModBlocks.init();
		ModItems.init();
		ModSounds.init();
		WellConfig.init();
		
		IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
		modbus.addListener(this::onClientSetup);
		modbus.addListener(WellConfig::configChanged);
		modbus.addListener(this::buildTabContents);
	}
	
	private void onClientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> BlockEntityRenderers.register(ModBlocks.WELL_BE.get(), WellRenderer::new));
	}
	
	private void buildTabContents(CreativeModeTabEvent.BuildContents event) {
		if (event.getTab() == CreativeModeTabs.COLORED_BLOCKS || event.getTab() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
			ModItems.ITEMS.getEntries().forEach(item -> event.accept(item));
		}
	}
	
}
