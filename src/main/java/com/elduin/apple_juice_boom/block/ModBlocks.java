package com.elduin.apple_juice_boom.block;

import com.elduin.apple_juice_boom.AppleJuiceBoom;

import java.util.function.Function;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public final class ModBlocks {

	/** A juice box. Shift-click it with an empty hand and stand back. */
	public static final Block APPLE_JUICE = register("apple_juice", AppleJuiceBlock::new,
			BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(0.3f).sound(SoundType.WOOL)
					.noOcclusion());

	public static final Item APPLE_JUICE_ITEM = registerBlockItem("apple_juice", APPLE_JUICE);

	private ModBlocks() {
	}

	/** Loads this class, which registers everything above. */
	public static void init() {
	}

	private static Block register(String name, Function<BlockBehaviour.Properties, Block> factory,
			BlockBehaviour.Properties properties) {
		ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, AppleJuiceBoom.id(name));
		return Registry.register(BuiltInRegistries.BLOCK, key, factory.apply(properties.setId(key)));
	}

	private static Item registerBlockItem(String name, Block block) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, AppleJuiceBoom.id(name));
		return Registry.register(BuiltInRegistries.ITEM, key,
				new BlockItem(block, new Item.Properties().setId(key).useBlockDescriptionPrefix()));
	}
}
