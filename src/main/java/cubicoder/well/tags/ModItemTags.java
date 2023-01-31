package cubicoder.well.tags;

import cubicoder.well.WellMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModItemTags {

	public static final TagKey<Item> WELLS = ItemTags.create(new ResourceLocation(WellMod.MODID, "wells"));
	
}
