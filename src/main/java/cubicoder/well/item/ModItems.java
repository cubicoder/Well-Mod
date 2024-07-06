package cubicoder.well.item;

import cubicoder.well.WellMod;
import cubicoder.well.block.ModBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(WellMod.MODID);
	
	public static final DeferredItem<BlockItem> WELL             = ITEMS.registerSimpleBlockItem(ModBlocks.WELL);
	public static final DeferredItem<BlockItem> WHITE_WELL       = fromColoredWell(ModBlocks.WHITE_WELL);
	public static final DeferredItem<BlockItem> ORANGE_WELL      = fromColoredWell(ModBlocks.ORANGE_WELL);
	public static final DeferredItem<BlockItem> MAGENTA_WELL     = fromColoredWell(ModBlocks.MAGENTA_WELL);
	public static final DeferredItem<BlockItem> LIGHT_BLUE_WELL  = fromColoredWell(ModBlocks.LIGHT_BLUE_WELL);
	public static final DeferredItem<BlockItem> YELLOW_WELL      = fromColoredWell(ModBlocks.YELLOW_WELL);
	public static final DeferredItem<BlockItem> LIME_WELL        = fromColoredWell(ModBlocks.LIME_WELL);
	public static final DeferredItem<BlockItem> PINK_WELL        = fromColoredWell(ModBlocks.PINK_WELL);
	public static final DeferredItem<BlockItem> GRAY_WELL        = fromColoredWell(ModBlocks.GRAY_WELL);
	public static final DeferredItem<BlockItem> LIGHT_GRAY_WELL  = fromColoredWell(ModBlocks.LIGHT_GRAY_WELL);
	public static final DeferredItem<BlockItem> CYAN_WELL        = fromColoredWell(ModBlocks.CYAN_WELL);
	public static final DeferredItem<BlockItem> PURPLE_WELL      = fromColoredWell(ModBlocks.PURPLE_WELL);
	public static final DeferredItem<BlockItem> BLUE_WELL        = fromColoredWell(ModBlocks.BLUE_WELL);
	public static final DeferredItem<BlockItem> BROWN_WELL       = fromColoredWell(ModBlocks.BROWN_WELL);
	public static final DeferredItem<BlockItem> GREEN_WELL       = fromColoredWell(ModBlocks.GREEN_WELL);
	public static final DeferredItem<BlockItem> RED_WELL         = fromColoredWell(ModBlocks.RED_WELL);
	public static final DeferredItem<BlockItem> BLACK_WELL       = fromColoredWell(ModBlocks.BLACK_WELL);

	public static void init(IEventBus modBus) {
        ITEMS.register(modBus);
    }

	
	private static DeferredItem<BlockItem> fromColoredWell(DeferredBlock<Block> block) {
		return ITEMS.register(block.getId().getPath(), () -> new ColoredWellBlockItem(block.get(), new Item.Properties()));
	}
	
}
