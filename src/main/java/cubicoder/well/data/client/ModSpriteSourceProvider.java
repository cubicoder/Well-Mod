package cubicoder.well.data.client;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import cubicoder.well.WellMod;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SpriteSourceProvider;

public class ModSpriteSourceProvider extends SpriteSourceProvider {

	public ModSpriteSourceProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper fileHelper) {
		super(output, lookupProvider, WellMod.MODID, fileHelper);
	}

	@Override
	protected void gather() {
		atlas(SpriteSourceProvider.BLOCKS_ATLAS).addSource(new SingleFile(new ResourceLocation("minecraft:entity/lead_knot"), Optional.empty()));
	}
}
