package cubicoder.well.tags;

import cubicoder.well.WellMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModBlockTags {

	public static final TagKey<Block> WELLS = BlockTags.create(ResourceLocation.fromNamespaceAndPath(WellMod.MODID, "wells"));
	
}
