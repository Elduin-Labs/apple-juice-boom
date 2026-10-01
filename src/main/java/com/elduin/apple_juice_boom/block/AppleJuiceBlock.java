package com.elduin.apple_juice_boom.block;

import com.elduin.apple_juice_boom.Compat;
import com.elduin.apple_juice_boom.net.BoomPayload;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A juice box of apple juice. Shift-click it with an empty hand to shake it: it fizzes for a couple
 * of seconds, then blows up. The world is saved first, and then the game of the player who shook
 * it crashes for real (see {@code client.Apocalypse}). Everyone else just sees a big explosion.
 */
public class AppleJuiceBlock extends Block {

	public static final BooleanProperty SHAKEN = BooleanProperty.create("shaken");

	/** How long it fizzes before it goes off, in ticks. */
	private static final int FUSE = 50;

	/** How big the bang is. TNT is 4. */
	private static final float POWER = 12.0F;

	private static final VoxelShape SHAPE = Block.box(4.0, 0.0, 5.0, 12.0, 12.0, 11.0);

	/** Who shook each fizzing juice box, so the right person's game crashes. */
	private static final Map<String, UUID> SHAKERS = new HashMap<>();

	public AppleJuiceBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(SHAKEN, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(SHAKEN);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (!player.isSecondaryUseActive()) {
			if (player instanceof ServerPlayer serverPlayer) {
				Compat.actionBar(serverPlayer, Component.translatable("block.apple_juice_boom.apple_juice.hint"));
			}
			return InteractionResult.SUCCESS;
		}
		if (state.getValue(SHAKEN)) {
			return InteractionResult.PASS;
		}
		if (level instanceof ServerLevel server) {
			server.setBlock(pos, state.setValue(SHAKEN, true), Block.UPDATE_ALL);
			SHAKERS.put(key(server, pos), player.getUUID());
			server.playSound(null, pos, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.5F);
			server.scheduleTick(pos, this, FUSE);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (state.getValue(SHAKEN)) {
			boom(level, pos);
		}
	}

	private static void boom(ServerLevel level, BlockPos pos) {
		UUID id = SHAKERS.remove(key(level, pos));
		Player shaker = id != null ? level.getPlayerByUUID(id) : null;
		if (shaker == null) {
			shaker = level.getNearestPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 32.0, false);
		}

		level.removeBlock(pos, false);
		// Save (and wait for it to reach the disk) before the bang, so the crash loses nothing.
		level.getServer().saveEverything(true, true, true);
		level.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, POWER, Level.ExplosionInteraction.TNT);

		if (shaker instanceof ServerPlayer player && ServerPlayNetworking.canSend(player, BoomPayload.TYPE)) {
			ServerPlayNetworking.send(player, new BoomPayload());
		}
	}

	/** Fizzing: juice spraying out of the straw. */
	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (!state.getValue(SHAKEN)) {
			return;
		}
		double x = pos.getX() + 0.5;
		double y = pos.getY() + 1.0;
		double z = pos.getZ() + 0.5;
		for (int i = 0; i < 4; i++) {
			level.addParticle(ParticleTypes.FALLING_HONEY, x + (random.nextDouble() - 0.5) * 0.3, y,
					z + (random.nextDouble() - 0.5) * 0.3, 0.0, 0.0, 0.0);
			level.addParticle(ParticleTypes.SPLASH, x, y, z,
					(random.nextDouble() - 0.5) * 0.3, 0.3, (random.nextDouble() - 0.5) * 0.3);
		}
		if (random.nextInt(3) == 0) {
			level.playLocalSound(x, y, z, SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, SoundSource.BLOCKS,
					1.0F, 0.8F + random.nextFloat() * 0.6F, false);
		}
	}

	private static String key(ServerLevel level, BlockPos pos) {
		return level.dimension() + "@" + pos.asLong();
	}
}
