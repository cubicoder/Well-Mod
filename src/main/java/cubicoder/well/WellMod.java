package cubicoder.well;

import cubicoder.well.block.ModBlocks;
import cubicoder.well.client.WellRenderer;
import cubicoder.well.config.WellConfig;
import cubicoder.well.item.ModItems;
import cubicoder.well.sound.ModSounds;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
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
	
	private void buildTabContents(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.COLORED_BLOCKS || event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
			event.accept(ModItems.WELL);
			event.accept(ModItems.WHITE_WELL);
			event.accept(ModItems.LIGHT_GRAY_WELL);
			event.accept(ModItems.GRAY_WELL);
			event.accept(ModItems.BLACK_WELL);
			event.accept(ModItems.BROWN_WELL);
			event.accept(ModItems.RED_WELL);
			event.accept(ModItems.ORANGE_WELL);
			event.accept(ModItems.YELLOW_WELL);
			event.accept(ModItems.LIME_WELL);
			event.accept(ModItems.GREEN_WELL);
			event.accept(ModItems.CYAN_WELL);
			event.accept(ModItems.LIGHT_BLUE_WELL);
			event.accept(ModItems.BLUE_WELL);
			event.accept(ModItems.PURPLE_WELL);
			event.accept(ModItems.MAGENTA_WELL);
			event.accept(ModItems.PINK_WELL);
		}
	}
	
}
