package com.zacharybarbanell.voidexpansion;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockStateMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

import java.util.List;

public class VoidExpansionFeatures {
    public static class ConfiguredFeatures {
        public static final ResourceKey<ConfiguredFeature<?, ?>> SKY_CRYSTAL_VEIN_CONFIGURED_KEY =
                ResourceKey.create(
                        Registries.CONFIGURED_FEATURE,
                        VoidExpansion.resourceLocation("ore_sky_crystal")
                );
        public static void configure(BootstrapContext<ConfiguredFeature<?, ?>> bootstrapContext) {
            RuleTest endStoneReplaceableRule = new BlockStateMatchTest(Blocks.END_STONE.defaultBlockState());
            List<OreConfiguration.TargetBlockState> skyCrystalOreConfig =
                    List.of(
                            OreConfiguration.target(endStoneReplaceableRule, VoidExpansionBlocks.SKY_CRYSTAL.defaultBlockState())
                    );
            bootstrapContext.register(SKY_CRYSTAL_VEIN_CONFIGURED_KEY, new ConfiguredFeature<>(
                    OreFeatureExposedBelow.INSTANCE,
                    new OreConfiguration(skyCrystalOreConfig, 24, 1.0F)
            ));
        }
    }

    public static class PlacedFeatures {
        public static final ResourceKey<PlacedFeature> SKY_CRYSTAL_VEIN_PLACED_KEY =
                ResourceKey.create(
                        Registries.PLACED_FEATURE,
                        VoidExpansion.resourceLocation("ore_sky_crystal")
                );

        public static void configure(BootstrapContext<PlacedFeature> bootstrapContext) {
            HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = bootstrapContext.lookup(Registries.CONFIGURED_FEATURE);
            bootstrapContext.register(
                    SKY_CRYSTAL_VEIN_PLACED_KEY,
                    new PlacedFeature(
                            configuredFeatures.getOrThrow(ConfiguredFeatures.SKY_CRYSTAL_VEIN_CONFIGURED_KEY),
                            List.of(
                                    RarityFilter.onAverageOnceEvery(30),
                                    InSquarePlacement.spread(),
                                    BiomeFilter.biome(),
                                    BelowWorldPlacement.placement()
                            )
                    )
            );
        }
    }
}
