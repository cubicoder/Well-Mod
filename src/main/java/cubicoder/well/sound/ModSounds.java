package cubicoder.well.sound;

import cubicoder.well.WellMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModSounds {

	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, WellMod.MODID);
	
	public static final Supplier<SoundEvent> CRANK = SOUND_EVENTS.register("block.well.crank", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(WellMod.MODID, "block.well.crank")));
	
	public static void init(IEventBus modBus) {
		SOUND_EVENTS.register(modBus);
	}
	
}
