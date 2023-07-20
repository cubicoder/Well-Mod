package cubicoder.well.item;

import cubicoder.well.WellMod;
import cubicoder.well.block.ModBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, WellMod.MODID);
	
	public static final RegistryObject<BlockItem> WELL             = fromBlock(ModBlocks.WELL);
	public static final RegistryObject<BlockItem> WHITE_WELL       = fromColoredWell(ModBlocks.WHITE_WELL);
	public static final RegistryObject<BlockItem> ORANGE_WELL      = fromColoredWell(ModBlocks.ORANGE_WELL);
	public static final RegistryObject<BlockItem> MAGENTA_WELL     = fromColoredWell(ModBlocks.MAGENTA_WELL);
	public static final RegistryObject<BlockItem> LIGHT_BLUE_WELL  = fromColoredWell(ModBlocks.LIGHT_BLUE_WELL);
	public static final RegistryObject<BlockItem> YELLOW_WELL      = fromColoredWell(ModBlocks.YELLOW_WELL);
	public static final RegistryObject<BlockItem> LIME_WELL        = fromColoredWell(ModBlocks.LIME_WELL);
	public static final RegistryObject<BlockItem> PINK_WELL        = fromColoredWell(ModBlocks.PINK_WELL);
	public static final RegistryObject<BlockItem> GRAY_WELL        = fromColoredWell(ModBlocks.GRAY_WELL);
	public static final RegistryObject<BlockItem> LIGHT_GRAY_WELL  = fromColoredWell(ModBlocks.LIGHT_GRAY_WELL);
	public static final RegistryObject<BlockItem> CYAN_WELL        = fromColoredWell(ModBlocks.CYAN_WELL);
	public static final RegistryObject<BlockItem> PURPLE_WELL      = fromColoredWell(ModBlocks.PURPLE_WELL);
	public static final RegistryObject<BlockItem> BLUE_WELL        = fromColoredWell(ModBlocks.BLUE_WELL);
	public static final RegistryObject<BlockItem> BROWN_WELL       = fromColoredWell(ModBlocks.BROWN_WELL);
	public static final RegistryObject<BlockItem> GREEN_WELL       = fromColoredWell(ModBlocks.GREEN_WELL);
	public static final RegistryObject<BlockItem> RED_WELL         = fromColoredWell(ModBlocks.RED_WELL);
	public static final RegistryObject<BlockItem> BLACK_WELL       = fromColoredWell(ModBlocks.BLACK_WELL);

	public static void init() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
	
	private static RegistryObject<BlockItem> fromBlock(RegistryObject<Block> block) {
		return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(),
				new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)));
	}
	
	private static RegistryObject<BlockItem> fromColoredWell(RegistryObject<Block> block) {
		return ITEMS.register(block.getId().getPath(), () -> new ColoredWellBlockItem(block.get(), 
				new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)));
	}
	
}
