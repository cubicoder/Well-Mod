package cubicoder.well.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;

public class ColoredWellBlockItem extends BlockItem {

	public ColoredWellBlockItem(Block block, Properties properties) {
		super(block, properties);
	}
	
	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
		if (context.getPlayer().isCrouching()) return super.onItemUseFirst(stack, context);
		
		World level = context.getLevel();
		PlayerEntity player = context.getPlayer();
		BlockState state = level.getBlockState(context.getClickedPos());
		if (state.getBlock() instanceof CauldronBlock) {
			if (!level.isClientSide && !player.isCreative()) {
				int cauldronLevel = state.getValue(CauldronBlock.LEVEL);
				if (cauldronLevel > 0) {
					player.getItemInHand(context.getHand()).shrink(1);
					player.awardStat(Stats.USE_CAULDRON);
					ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(ModItems.WELL.get()));
					((CauldronBlock) state.getBlock()).setWaterLevel(level, context.getClickedPos(), state, cauldronLevel - 1);
				}
			}
			
			return ActionResultType.SUCCESS;
		}
		
		return super.onItemUseFirst(stack, context);
	}
	
}
