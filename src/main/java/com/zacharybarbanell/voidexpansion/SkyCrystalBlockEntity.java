package com.zacharybarbanell.voidexpansion;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SkyCrystalBlockEntity extends BlockEntity {
    public SkyCrystalBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public SkyCrystalBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(VoidExpansion.SKY_CRYSTAL_BE_TYPE, blockPos, blockState);
    }

    public boolean shouldRenderFace(Direction direction) {
        return Block.shouldRenderFace(this.getBlockState(), this.level, this.getBlockPos(), direction, this.getBlockPos().relative(direction));
    }
}
