package cubicoder.well.client;

import cubicoder.well.block.ModBlocks;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class ClientEvents {

	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(ModBlocks.WELL_BE.get(), WellRenderer::new);
	}

}
