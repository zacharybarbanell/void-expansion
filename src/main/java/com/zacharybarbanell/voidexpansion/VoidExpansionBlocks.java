package com.zacharybarbanell.voidexpansion;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.function.UnaryOperator;

public abstract class VoidExpansionBlocks {
    public static final Block SKY_CRYSTAL = register(
            "sky_crystal",
            new SkyCrystalBlock(
                    UniformInt.of(4,9),
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
            ),
            properties -> properties.component(VoidExpansion.FALLS_UP, Unit.INSTANCE)
    );
    public static final Block VOID_BLOCK = register(
            "void_block",
            new Block(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_PURPLE)
                            .requiresCorrectToolForDrops()
                            .strength(4.0f, 2.0f)
                            .sound(SoundType.METAL)
            ),
            properties -> properties.component(VoidExpansion.VOID_IMMUNE, Unit.INSTANCE)
    );
    public static final Block ANTIGRAVITY_PROJECTOR = register(
            "antigravity_projector",
            new AntigravityProjectorBlock(
                    BlockBehaviour.Properties.of() //TODO
            )
    );
    public static final Block ANTIGRAVITY_BEAM = register(
            "antigravity_beam",
            new AntigravityBeamBlock(
                    BlockBehaviour.Properties.of()
                            .replaceable()
                            .noCollission()
                            .noLootTable()
                            .pushReaction(PushReaction.DESTROY)
                            .sound(SoundType.EMPTY)
                            .noTerrainParticles()
            ),
            false
    );

    public static void initialize() {}

    private static Block register(String name, Block block) {
        return register(name, block, true);
    }

    private static Block register(String name, Block block, Boolean shouldRegisterItem) {
        return register(name, block, shouldRegisterItem, UnaryOperator.identity());
    }

    private static Block register(String name, Block block, UnaryOperator<Item.Properties> blockItemOperator) {
        ResourceLocation resourceLocation = VoidExpansion.resourceLocation(name);

        return register(name, block, true, blockItemOperator);
    }

    private static Block register(String name, Block block, Boolean shouldRegisterItem, UnaryOperator<Item.Properties> blockItemOperator) {
        ResourceLocation resourceLocation = VoidExpansion.resourceLocation(name);

        if (shouldRegisterItem) {
            BlockItem blockItem = new BlockItem(block, blockItemOperator.apply(new Item.Properties()));
            Registry.register(BuiltInRegistries.ITEM, resourceLocation, blockItem);
        }

        return Registry.register(BuiltInRegistries.BLOCK, resourceLocation, block);
    }
}
