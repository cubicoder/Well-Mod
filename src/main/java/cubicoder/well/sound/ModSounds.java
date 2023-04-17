package cubicoder.well.sound;

import cubicoder.well.WellMod;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModSounds {

	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, WellMod.MODID);
	
	public static final RegistryObject<SoundEvent> CRANK = SOUND_EVENTS.register("block.well.crank", () -> new SoundEvent(new ResourceLocation(WellMod.MODID, "block.well.crank")));
	
	public static void init() {
		SOUND_EVENTS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
}
