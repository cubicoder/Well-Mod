package cubicoder.well.data.client;

import java.util.Optional;

import cubicoder.well.WellMod;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SpriteSourceProvider;

public class ModSpriteSourceProvider extends SpriteSourceProvider {

	public ModSpriteSourceProvider(PackOutput output, ExistingFileHelper fileHelper) {
		super(output, fileHelper, WellMod.MODID);
	}

	@Override
	protected void addSources() {
		atlas(SpriteSourceProvider.BLOCKS_ATLAS).addSource(new SingleFile(new ResourceLocation("minecraft:entity/lead_knot"), Optional.empty()));
	}

}
