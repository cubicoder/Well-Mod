package cubicoder.well.config;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class WellConfig {

	private static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();
	private static final ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();
	
	public static ModConfigSpec.BooleanValue playSound = CLIENT_BUILDER
			.comment("Play the well cranking sound when someone takes fluid from a well.")
			.translation("config.well.playSound")
			.define("playSound", true);
	
	public static ModConfigSpec.IntValue tankCapacity = SERVER_BUILDER
			.comment("How many millibuckets of a fluid can wells hold? Set to 0 to disable.")
			.translation("config.well.tankCapacity")
			.worldRestart()
			.defineInRange("tankCapacity", 100000, 0, Integer.MAX_VALUE);
	public static ModConfigSpec.BooleanValue onlyOnePerChunk = SERVER_BUILDER
			.comment("When set to true, all wells in the chunk will stop working while there's more than 1.")
			.translation("config.well.onlyOnePerChunk")
			.define("onlyOnePerChunk", false);
	public static ModConfigSpec.BooleanValue allowWellFill = SERVER_BUILDER
			.comment("When set to true, wells can be filled by buckets and pipes. Otherwise, wells can only be extracted from.")
			.translation("config.well.allowWellFill")
			.define("allowWellFill", false);
	
	private WellConfig() {}
	
	public static void init(ModContainer modContainer) {
		modContainer.registerConfig(ModConfig.Type.CLIENT, CLIENT_BUILDER.build());
		modContainer.registerConfig(ModConfig.Type.SERVER, SERVER_BUILDER.build());
	}
	
}
