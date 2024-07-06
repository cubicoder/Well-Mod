package cubicoder.well.data.common;

import cubicoder.well.block.ModBlocks;
import cubicoder.well.block.WellBlock;
import net.minecraft.core.Holder;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.Collections;

public class ModBlockLootSubProvider extends BlockLootSubProvider {

	public ModBlockLootSubProvider() {
		super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags());
	}

	@Override
	protected void generate() {
		addWell(ModBlocks.WELL.get());
		addWell(ModBlocks.WHITE_WELL.get());
		addWell(ModBlocks.ORANGE_WELL.get());
		addWell(ModBlocks.MAGENTA_WELL.get());
		addWell(ModBlocks.LIGHT_BLUE_WELL.get());
		addWell(ModBlocks.YELLOW_WELL.get());
		addWell(ModBlocks.LIME_WELL.get());
		addWell(ModBlocks.PINK_WELL.get());
		addWell(ModBlocks.GRAY_WELL.get());
		addWell(ModBlocks.LIGHT_GRAY_WELL.get());
		addWell(ModBlocks.CYAN_WELL.get());
		addWell(ModBlocks.PURPLE_WELL.get());
		addWell(ModBlocks.BLUE_WELL.get());
		addWell(ModBlocks.BROWN_WELL.get());
		addWell(ModBlocks.GREEN_WELL.get());
		addWell(ModBlocks.RED_WELL.get());
		addWell(ModBlocks.BLACK_WELL.get());
	}

	@Override
	protected Iterable<Block> getKnownBlocks() {
		return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value).toList();
	}
	
	private void addWell(Block block) {
		this.add(block, (b) -> createSinglePropConditionTable(block, WellBlock.HALF, DoubleBlockHalf.LOWER));
	}
	
}
