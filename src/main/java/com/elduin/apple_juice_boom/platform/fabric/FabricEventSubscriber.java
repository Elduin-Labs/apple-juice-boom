package com.elduin.apple_juice_boom.platform.fabric;

//? fabric {

import com.elduin.apple_juice_boom.block.ModBlocks;
import net.minecraft.world.item.CreativeModeTabs;
//? if >=26 {
/*import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
*///? } else {
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
//? }

public class FabricEventSubscriber {

	public static void registerEvents() {
		// Fabric API renamed "item groups" to "creative mode tabs" in 26.
		//? if >=26 {
		/*CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS)
				.register(output -> output.accept(ModBlocks.APPLE_JUICE_ITEM));
		*///? } else {
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FOOD_AND_DRINKS)
				.register(entries -> entries.accept(ModBlocks.APPLE_JUICE_ITEM));
		//? }
	}
}
//?}
