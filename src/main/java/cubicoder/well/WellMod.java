package cubicoder.well;

import cubicoder.well.block.ModBlocks;
import cubicoder.well.client.ClientEvents;
import cubicoder.well.config.WellConfig;
import cubicoder.well.data.DataGenerators;
import cubicoder.well.item.ModItems;
import cubicoder.well.sound.ModSounds;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@Mod(WellMod.MODID)
public final class WellMod {
	
	public static final String MODID = "well";
	public static final String MOD_NAME = "Well Mod";

	public WellMod(IEventBus modBus, ModContainer modContainer) {
		ModBlocks.init(modBus);
		ModItems.init(modBus);
		ModSounds.init(modBus);
		WellConfig.init(modContainer);
		
		if (FMLEnvironment.dist == Dist.CLIENT) modBus.addListener(ClientEvents::registerEntityRenderers);
		modBus.addListener(DataGenerators::gatherData);
		modBus.addListener(WellConfig::configChanged);
		modBus.addListener(this::buildTabContents);
		modBus.addListener(this::registerCapabilities);
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

	private void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlocks.WELL_BE.get(), (be, context) -> be.getTank());
	}

}
