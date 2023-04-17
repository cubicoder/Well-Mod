package cubicoder.well.tags;

import cubicoder.well.WellMod;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags.IOptionalNamedTag;

public class ModItemTags {

	public static final IOptionalNamedTag<Item> WELLS = ItemTags.createOptional(new ResourceLocation(WellMod.MODID, "wells"));
	
}
