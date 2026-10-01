package com.elduin.apple_juice_boom.platform.fabric;

//? fabric {

import com.elduin.apple_juice_boom.AppleJuiceBoom;
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import net.fabricmc.api.ModInitializer;

@Entrypoint("main")
public class FabricEntrypoint implements ModInitializer {

	@Override
	public void onInitialize() {
		AppleJuiceBoom.onInitialize();
		FabricEventSubscriber.registerEvents();
	}
}
//?}
