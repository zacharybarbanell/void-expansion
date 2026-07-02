package com.zacharybarbanell.voidexpansion;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

public class AntigravityProjectorBlock extends Block {
    public static final MapCodec<AntigravityProjectorBlock> CODEC = AntigravityProjectorBlock.simpleCodec(AntigravityProjectorBlock::new);

    @Override
    protected MapCodec<AntigravityProjectorBlock> codec() {
        return CODEC;
    }

    protected AntigravityProjectorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void updateIndirectNeighbourShapes(BlockState state, LevelAccessor level, BlockPos pos, int updateFlags, int updateLimit) {
        //VoidExpansion.LOGGER.warn("updateIndirectNeighbourShapes {} {} {} {}", state, pos, level.getBlockState(pos), level.isClientSide());
        if (level.isClientSide()) {
            return;
        }
        updateBeam(state, level, pos);
    }

    @Override
    protected BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        //VoidExpansion.LOGGER.warn("updateShape {} {} {} {} {}", blockState, direction, blockPos, blockPos2, levelAccessor.isClientSide());
        if (direction != Direction.UP) {
            return blockState;
        }
        updateBeam(blockState, levelAccessor, blockPos);
        return blockState;
    }

    private void updateBeam(BlockState state, LevelAccessor level, BlockPos pos) {
        if (level.getBlockState(pos) == state) {
            AntigravityBeamBlock.placeAt(level, pos.above(), AntigravityBeamBlock.MAX_LENGTH - 1);
        }
        else {
            BlockPos indirectPos = pos.above(2);
            if (level.getBlockState(indirectPos).is(VoidExpansionBlocks.ANTIGRAVITY_BEAM)) {
                AntigravityBeamBlock.removeAt(level, indirectPos);
            }
        }
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        //VoidExpansion.LOGGER.info("onPlace {} {} {} {}", state, oldState, level, movedByPiston);
    }
}
