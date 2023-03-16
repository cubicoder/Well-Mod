package cubicoder.well;

import cubicoder.well.block.ModBlocks;
import cubicoder.well.client.WellRenderer;
import cubicoder.well.config.WellConfig;
import cubicoder.well.item.ModItems;
import cubicoder.well.sound.ModSounds;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
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
	}
	
	private void onClientSetup(final FMLClientSetupEvent event) {
		event.enqueueWork(() -> BlockEntityRenderers.register(ModBlocks.WELL_BE.get(), WellRenderer::new));
	}
	
}
