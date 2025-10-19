package com.zacharybarbanell.voidexpansion;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class SkyCrystalRenderer implements BlockEntityRenderer<SkyCrystalBlockEntity> {
    public SkyCrystalRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public void render(SkyCrystalBlockEntity blockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {

    }
}
