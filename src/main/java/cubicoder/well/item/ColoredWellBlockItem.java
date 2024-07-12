package cubicoder.well.item;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class ColoredWellBlockItem extends BlockItem {

	public ColoredWellBlockItem(Block block, Properties properties) {
		super(block, properties);
	}
	
	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		Player player = context.getPlayer();
		BlockState state = level.getBlockState(context.getClickedPos());
		if (state.getBlock() instanceof LayeredCauldronBlock) {
			// get cauldron fluid
			IFluidHandler cauldronHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, context.getClickedPos(), state, null, context.getClickedFace());
			if (cauldronHandler != null && cauldronHandler.getFluidInTank(0).getFluid().isSame(Fluids.WATER)) {
				if (!level.isClientSide()) {
					int cauldronLevel = state.getValue(LayeredCauldronBlock.LEVEL);
					if (cauldronLevel > 0) {
						assert player != null;
						player.getItemInHand(context.getHand()).shrink(1);
						player.awardStat(Stats.USE_CAULDRON);
						ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(ModItems.WELL.get()));
						LayeredCauldronBlock.lowerFillLevel(state, level, context.getClickedPos());
					}
				}
				return InteractionResult.sidedSuccess(level.isClientSide());
			}
		}
		return super.useOn(context);
	}
	
}
