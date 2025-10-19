package com.zacharybarbanell.voidexpansion;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SkyCrystalBlock extends HalfTransparentBlock implements EntityBlock {
    public SkyCrystalBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new SkyCrystalBlockEntity(blockPos, blockState);
    }

    @Override
    protected RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.INVISIBLE;
    }
}
