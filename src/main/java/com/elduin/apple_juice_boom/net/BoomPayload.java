package com.elduin.apple_juice_boom.net;

import com.elduin.apple_juice_boom.AppleJuiceBoom;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Server -> the one player who shook the juice: the sky explodes, then your game crashes. */
public record BoomPayload() implements CustomPacketPayload {

	public static final Type<BoomPayload> TYPE = new Type<>(AppleJuiceBoom.id("boom"));
	public static final StreamCodec<FriendlyByteBuf, BoomPayload> CODEC = StreamCodec.unit(new BoomPayload());

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
