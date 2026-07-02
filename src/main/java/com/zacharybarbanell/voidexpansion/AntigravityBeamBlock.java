package com.zacharybarbanell.voidexpansion;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AntigravityBeamBlock extends TransparentBlock {
    public static final MapCodec<AntigravityBeamBlock> CODEC = AntigravityBeamBlock.simpleCodec(AntigravityBeamBlock::new);
    public static final int MAX_LENGTH = 16;
    public static final IntegerProperty LENGTH = IntegerProperty.create("length", 0, MAX_LENGTH - 1);

    protected AntigravityBeamBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(LENGTH, 0));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LENGTH);
    }

    @Override
    protected VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return Shapes.empty();
    }

    public static void placeAt(LevelAccessor level, BlockPos pos, int length) {
        if (pos.getY() > level.getMaxBuildHeight() || length < 0) {
            return;
        }
        BlockState targetState = VoidExpansionBlocks.ANTIGRAVITY_BEAM.defaultBlockState().setValue(LENGTH, length);
        if (level.getBlockState(pos) == targetState) {
            return;
        } else if (level.getBlockState(pos).isAir()) {
            level.setBlock(pos, targetState, Block.UPDATE_ALL);
            placeAt(level, pos.above(), length - 1);
        } else if (length == MAX_LENGTH - 1) {
            placeAt(level, pos.above(), length - 1);
        }
    }

    public static void removeAt(LevelAccessor level, BlockPos pos) {
        if (level.getBlockState(pos).is(VoidExpansionBlocks.ANTIGRAVITY_BEAM)) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    protected BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        if (direction == Direction.DOWN) {
            int value = -1;
            if (blockState2.is(VoidExpansionBlocks.ANTIGRAVITY_BEAM)) {
                value = blockState2.getValue(LENGTH) - 1;
            } else if (blockState2.is(VoidExpansionBlocks.ANTIGRAVITY_PROJECTOR)) {
                value = MAX_LENGTH - 1;
            }
            BlockState twiceBelow = levelAccessor.getBlockState(blockPos.below(2));
            if (twiceBelow.is(VoidExpansionBlocks.ANTIGRAVITY_PROJECTOR)) {
                value = Math.max(value, MAX_LENGTH - 2);
            }
            if (value >= 0) {
                return VoidExpansionBlocks.ANTIGRAVITY_BEAM.defaultBlockState().setValue(LENGTH, value);
            } else {
                return Blocks.AIR.defaultBlockState();
            }
        } else if (direction == Direction.UP && blockState2.isAir()) {
            placeAt(levelAccessor, blockPos2, blockState.getValue(LENGTH) - 1);
            return blockState;
        } else {
            return blockState;
        }
    }

    @Override
    protected void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        ((EnableAntigravity) entity).void_expansion$enableAntigravity();
        super.entityInside(blockState, level, blockPos, entity);
    }

    public interface EnableAntigravity {
        void void_expansion$enableAntigravity();
        boolean void_expansion$getAntigravity();
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
        AABB aabb = new AABB(blockPos);
        for (Direction direction : Direction.values()) {
            if (direction != Direction.DOWN && !level.getBlockState(blockPos.offset(direction.getNormal())).is(VoidExpansionBlocks.ANTIGRAVITY_BEAM)) {
                Vec3 contract = Vec3.ZERO.relative(direction, 2D/16D);
                aabb = aabb.contract(contract.x, contract.y, contract.z);
            }
        }
        if (randomSource.nextFloat() < aabb.getXsize() * aabb.getYsize() * aabb.getZsize()) {
            level.addParticle(
                    VoidExpansion.ANTIGRAVITY_PARTICLE,
                    Mth.lerp(randomSource.nextFloat(), aabb.minX, aabb.maxX),
                    Mth.lerp(randomSource.nextFloat(), aabb.minY, aabb.maxY),
                    Mth.lerp(randomSource.nextFloat(), aabb.minZ, aabb.maxZ),
                    randomSource.nextGaussian() * 0.005,
                    randomSource.nextGaussian() * 0.001,
                    randomSource.nextGaussian() * 0.005
            );
        }
    }
}
