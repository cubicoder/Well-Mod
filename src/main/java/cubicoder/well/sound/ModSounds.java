package cubicoder.well.sound;

import cubicoder.well.WellMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {

	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, WellMod.MODID);
	
	public static final RegistryObject<SoundEvent> CRANK = SOUND_EVENTS.register("block.well.crank", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(WellMod.MODID, "block.well.crank")));
	
	public static void init() {
		SOUND_EVENTS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
}
