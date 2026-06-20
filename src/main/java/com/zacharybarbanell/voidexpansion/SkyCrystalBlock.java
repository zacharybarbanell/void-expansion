package com.zacharybarbanell.voidexpansion;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockState;

public class SkyCrystalBlock extends TransparentBlock {
    private final IntProvider xpRange;

    public SkyCrystalBlock(IntProvider intProvider, Properties properties) {
        super(properties);
        xpRange = intProvider;
    }

    @Override
    protected void spawnAfterBreak(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, ItemStack itemStack, boolean bl) {
        super.spawnAfterBreak(blockState, serverLevel, blockPos, itemStack, bl);
        if (bl) {
            this.tryDropExperience(serverLevel, blockPos, itemStack, this.xpRange);
        }
    }
}
