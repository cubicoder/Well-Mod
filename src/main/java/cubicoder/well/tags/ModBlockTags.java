package cubicoder.well.tags;

import cubicoder.well.WellMod;
import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags.IOptionalNamedTag;

public class ModBlockTags {

	public static final IOptionalNamedTag<Block> WELLS = BlockTags.createOptional(new ResourceLocation(WellMod.MODID, "wells"));
	
}
