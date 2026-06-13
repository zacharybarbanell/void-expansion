package com.zacharybarbanell.voidexpansion;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public abstract class VoidExpansionBlocks {
    public static final Block SKY_CRYSTAL = register(
            "sky_crystal",
            new SkyCrystalBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_PURPLE)
                            .strength(1.5f)
                            .sound(SoundType.AMETHYST)
                            .noOcclusion()
                            .isValidSpawn(Blocks::never)
                            .isRedstoneConductor(Blocks::never)
                            .isSuffocating(Blocks::never)
                            .isViewBlocking(Blocks::never)
                            .requiresCorrectToolForDrops()
            )
    );
    public static final Block VOID_BLOCK = register(
            "void_block",
            new Block(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_PURPLE)
                            .requiresCorrectToolForDrops()
                            .strength(4.0f, 2.0f)
                            .sound(SoundType.METAL)
            )
    );

    public static void initialize() {}

    private static Block register(String name, Block block) {
        return register(name, block, true);
    }

    private static Block register(String name, Block block, Boolean shouldRegisterItem) {
        ResourceLocation resourceLocation = VoidExpansion.resourceLocation(name);

        if (shouldRegisterItem) {
            BlockItem blockItem = new BlockItem(block, new Item.Properties());
            Registry.register(BuiltInRegistries.ITEM, resourceLocation, blockItem);
        }

        return Registry.register(BuiltInRegistries.BLOCK, resourceLocation, block);
    }
}
