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
import net.minecraftforge.items.ItemHandlerHelper;

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
			if (!level.isClientSide && !player.isCreative()) {
				int cauldronLevel = state.getValue(LayeredCauldronBlock.LEVEL);
				if (cauldronLevel > 0) {
					player.getItemInHand(context.getHand()).shrink(1);
					player.awardStat(Stats.USE_CAULDRON);
					ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(ModItems.WELL.get()));
					LayeredCauldronBlock.lowerFillLevel(state, level, context.getClickedPos());
				}
			}
			
			return InteractionResult.SUCCESS;
		}
		
		return super.useOn(context);
	}
	
}
