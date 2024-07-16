package cubicoder.well.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cubicoder.well.WellMod;
import cubicoder.well.block.entity.WellBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
	
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(WellMod.MODID);
	public static final DeferredRegister<MapCodec<? extends Block>> BLOCK_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_TYPE, WellMod.MODID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, WellMod.MODID);
	
	public static final DeferredBlock<Block> WELL = BLOCKS.register("well", () -> new WellBlock(DyeColor.RED));
	public static final DeferredBlock<Block> WHITE_WELL = BLOCKS.register("white_well", () -> new WellBlock(DyeColor.WHITE));
	public static final DeferredBlock<Block> ORANGE_WELL = BLOCKS.register("orange_well", () -> new WellBlock(DyeColor.ORANGE));
	public static final DeferredBlock<Block> MAGENTA_WELL = BLOCKS.register("magenta_well", () -> new WellBlock(DyeColor.MAGENTA));
	public static final DeferredBlock<Block> LIGHT_BLUE_WELL = BLOCKS.register("light_blue_well", () -> new WellBlock(DyeColor.LIGHT_BLUE));
	public static final DeferredBlock<Block> YELLOW_WELL = BLOCKS.register("yellow_well", () -> new WellBlock(DyeColor.YELLOW));
	public static final DeferredBlock<Block> LIME_WELL = BLOCKS.register("lime_well", () -> new WellBlock(DyeColor.LIME));
	public static final DeferredBlock<Block> PINK_WELL = BLOCKS.register("pink_well", () -> new WellBlock(DyeColor.PINK));
	public static final DeferredBlock<Block> GRAY_WELL = BLOCKS.register("gray_well", () -> new WellBlock(DyeColor.GRAY));
	public static final DeferredBlock<Block> LIGHT_GRAY_WELL = BLOCKS.register("light_gray_well", () -> new WellBlock(DyeColor.LIGHT_GRAY));
	public static final DeferredBlock<Block> CYAN_WELL = BLOCKS.register("cyan_well", () -> new WellBlock(DyeColor.CYAN));
	public static final DeferredBlock<Block> PURPLE_WELL = BLOCKS.register("purple_well", () -> new WellBlock(DyeColor.PURPLE));
	public static final DeferredBlock<Block> BLUE_WELL = BLOCKS.register("blue_well", () -> new WellBlock(DyeColor.BLUE));
	public static final DeferredBlock<Block> BROWN_WELL = BLOCKS.register("brown_well", () -> new WellBlock(DyeColor.BROWN));
	public static final DeferredBlock<Block> GREEN_WELL = BLOCKS.register("green_well", () -> new WellBlock(DyeColor.GREEN));
	public static final DeferredBlock<Block> RED_WELL = BLOCKS.register("red_well", () -> new WellBlock(DyeColor.RED));
	public static final DeferredBlock<Block> BLACK_WELL = BLOCKS.register("black_well", () -> new WellBlock(DyeColor.BLACK));
	
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<WellBlock>> WELL_CODEC = BLOCK_TYPES.register(
			"well",
			() -> RecordCodecBuilder.mapCodec(instance ->
					instance.group(DyeColor.CODEC.fieldOf("color").forGetter(WellBlock::getColor)).apply(instance, WellBlock::new)));
	
	public static final Supplier<BlockEntityType<WellBlockEntity>> WELL_BE = BLOCK_ENTITIES.register("well",
			() -> BlockEntityType.Builder.of(WellBlockEntity::new, WELL.get(), WHITE_WELL.get(), ORANGE_WELL.get(),
					MAGENTA_WELL.get(), LIGHT_BLUE_WELL.get(), YELLOW_WELL.get(), LIME_WELL.get(), PINK_WELL.get(),
					GRAY_WELL.get(), LIGHT_GRAY_WELL.get(), CYAN_WELL.get(), PURPLE_WELL.get(), BLUE_WELL.get(),
					BROWN_WELL.get(), GREEN_WELL.get(), RED_WELL.get(), BLACK_WELL.get()).build(null));
	
	public static void init(IEventBus modBus) {
		BLOCKS.register(modBus);
		BLOCK_TYPES.register(modBus);
		BLOCK_ENTITIES.register(modBus);
	}
	
}
