package cubicoder.well.recipe;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.biome.Biome;

public record WellRecipeInput(Holder<Biome> biome) implements RecipeInput {
	
	@Override
	public ItemStack getItem(int index) {
		return ItemStack.EMPTY;
	}
	
	@Override
	public int size() {
		return 1;
	}
	
	@Override
	public boolean isEmpty() {
		return false;
	}
	
}
