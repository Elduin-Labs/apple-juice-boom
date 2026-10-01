package com.elduin.apple_juice_boom;

import com.elduin.apple_juice_boom.net.BoomPayload;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

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

	/** The line just above the hotbar. */
	public static void actionBar(ServerPlayer player, Component text) {
		//? if >=26 {
		/*player.sendOverlayMessage(text);
		*///? } else {
		player.displayClientMessage(text, true);
		//? }
	}
}
