package cubicoder.well.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.FluidStack;

public class WellRecipeSerializer implements RecipeSerializer<WellRecipe> {
	
	public static final MapCodec<WellRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
			ResourceLocation.CODEC.listOf().fieldOf("biomes").forGetter(WellRecipe::biomes),
			TagKey.codec(Registries.BIOME).listOf().fieldOf("biomeTags").forGetter(WellRecipe::biomeTags),
			Codec.INT.fieldOf("minTicks").forGetter(WellRecipe::minTicks),
			Codec.INT.fieldOf("maxTicks").forGetter(WellRecipe::maxTicks),
			FluidStack.CODEC.fieldOf("result").forGetter(WellRecipe::result)
	).apply(inst, WellRecipe::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, WellRecipe> STREAM_CODEC =
			ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());
	
	@Override
	public MapCodec<WellRecipe> codec() {
		return CODEC;
	}
	
	@Override
	public StreamCodec<RegistryFriendlyByteBuf, WellRecipe> streamCodec() {
		return STREAM_CODEC;
	}
	
}
