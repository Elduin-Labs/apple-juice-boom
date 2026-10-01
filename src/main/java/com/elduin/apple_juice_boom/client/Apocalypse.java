package com.elduin.apple_juice_boom.client;

import com.elduin.apple_juice_boom.net.BoomPayload;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

/**
 * What the player who shook the juice sees: three seconds of the whole sky exploding around them,
 * the screen shaking harder and harder, and then the game crashes for real with a crash report.
 */
public final class Apocalypse {

	/** How long the sky explodes before the crash, in ticks. */
	private static final int LENGTH = 60;

	private static int ticksLeft = -1;

	private Apocalypse() {
	}

	public static void register() {
		ClientPlayNetworking.registerGlobalReceiver(BoomPayload.TYPE, (payload, context) -> start());
		ClientTickEvents.END_CLIENT_TICK.register(Apocalypse::tick);
	}

	private static void start() {
		if (ticksLeft < 0) {
			ticksLeft = LENGTH;
		}
	}

	private static void tick(Minecraft minecraft) {
		if (ticksLeft < 0) {
			return;
		}
		ClientLevel level = minecraft.level;
		LocalPlayer player = minecraft.player;
		if (level == null || player == null) {
			ticksLeft = -1;
			return;
		}

		RandomSource random = level.getRandom();
		float progress = 1.0F - (float) ticksLeft / LENGTH;

		// More and more explosions, everywhere you look.
		int count = 1 + (int) (progress * 5);
		for (int i = 0; i < count; i++) {
			double angle = random.nextDouble() * Mth.TWO_PI;
			double distance = 6.0 + random.nextDouble() * 40.0;
			double x = player.getX() + Math.cos(angle) * distance;
			double y = player.getY() - 4.0 + random.nextDouble() * 36.0;
			double z = player.getZ() + Math.sin(angle) * distance;
			level.addParticle(ParticleTypes.EXPLOSION_EMITTER, true, true, x, y, z, 0.0, 0.0, 0.0);
		}
		if (ticksLeft % 3 == 0) {
			level.playLocalSound(player.getX() + random.nextGaussian() * 8.0, player.getY(),
					player.getZ() + random.nextGaussian() * 8.0, SoundEvents.GENERIC_EXPLODE.value(),
					SoundSource.BLOCKS, 4.0F, 0.5F + random.nextFloat() * 0.4F, false);
		}

		// The screen shakes harder as it goes.
		float shake = 1.0F + progress * 4.0F;
		player.setYRot(player.getYRot() + (random.nextFloat() - 0.5F) * shake);
		player.setXRot(Mth.clamp(player.getXRot() + (random.nextFloat() - 0.5F) * shake, -90.0F, 90.0F));

		if (--ticksLeft == 0) {
			ticksLeft = -1;
			minecraft.delayCrash(CrashReport.forThrowable(new TooMuchAppleJuiceException(), "Shaking a juice box"));
		}
	}

	/** Shows up at the top of the crash report. */
	static final class TooMuchAppleJuiceException extends RuntimeException {
		TooMuchAppleJuiceException() {
			super("The apple juice exploded and took Minecraft with it! (This crash is on purpose. Your world was saved.)");
		}
	}
}
