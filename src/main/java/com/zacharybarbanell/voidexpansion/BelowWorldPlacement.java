package com.zacharybarbanell.voidexpansion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.stream.Stream;

public class BelowWorldPlacement extends PlacementModifier {
    private static final BelowWorldPlacement INSTANCE = new BelowWorldPlacement();
    public static final MapCodec<BelowWorldPlacement> CODEC = MapCodec.unit(() -> INSTANCE);

    public static BelowWorldPlacement placement() {
        return INSTANCE;
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext placementContext, RandomSource randomSource, BlockPos blockPos) {
        WorldGenLevel worldGenLevel = placementContext.getLevel();
        blockPos = blockPos.atY(worldGenLevel.getMinBuildHeight());
        while (blockPos.getY() <= worldGenLevel.getMaxBuildHeight() && worldGenLevel.getBlockState(blockPos).isAir()) {
            blockPos = blockPos.above();
        }
        return Stream.of(blockPos);
    }

    @Override
    public PlacementModifierType<?> type() {
        return VoidExpansion.BELOW_WORLD_PLACEMENT;
    }
}
