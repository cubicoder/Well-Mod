package cubicoder.well.recipe;

import cubicoder.well.WellMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WellRecipeRegistration {

	public static final String RECIPE_NAME = "well";
	
	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, WellMod.MODID);
	public static final Supplier<RecipeType<WellRecipe>> WELL_RECIPE_TYPE = RECIPE_TYPES.register(RECIPE_NAME, () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(WellMod.MODID, RECIPE_NAME)));

	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, WellMod.MODID);
	public static final Supplier<RecipeSerializer<WellRecipe>> WELL_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register(RECIPE_NAME, WellRecipeSerializer::new);
	
	public static void init(IEventBus modBus) {
		RECIPE_TYPES.register(modBus);
		RECIPE_SERIALIZERS.register(modBus);
	}
	
}
