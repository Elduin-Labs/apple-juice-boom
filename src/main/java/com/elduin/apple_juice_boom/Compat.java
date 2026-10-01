package com.elduin.apple_juice_boom;

import com.elduin.apple_juice_boom.net.BoomPayload;

//? if >=26 {
/*import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
*///? } else {
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
//? }
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;

/**
 * The handful of things Mojang and Fabric moved between the versions this mod supports. Keeping
 * them in one place means the rest of the mod reads the same on every version.
 */
public final class Compat {

	private Compat() {
	}

	/** Fabric renamed "play S2C" to "clientbound play" in 26.2. */
	public static void registerPayloads() {
		//? if >=26.2 {
		/*PayloadTypeRegistry.clientboundPlay().register(BoomPayload.TYPE, BoomPayload.CODEC);
		*///? } else {
		PayloadTypeRegistry.playS2C().register(BoomPayload.TYPE, BoomPayload.CODEC);
		//? }
	}

	/** Fabric renamed its creative-tab builder in 26. */
	public static CreativeModeTab.Builder tabBuilder() {
		//? if >=26 {
		/*return FabricCreativeModeTab.builder();
		*///? } else {
		return FabricItemGroup.builder();
		//? }
	}

	/** The line just above the hotbar. */
	public static void actionBar(ServerPlayer player, Component text) {
		//? if >=26 {
		/*player.sendOverlayMessage(text);
		*///? } else {
		player.displayClientMessage(text, true);
		//? }
	}
}
