package com.zacharybarbanell.voidexpansion;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import org.jetbrains.annotations.Nullable;

public class VoidExpansionClient implements ClientModInitializer {
    @Nullable
    public static ShaderInstance rendertypeHalfTranslucent;
    public static final RenderStateShard.ShaderStateShard RENDERTYPE_HALF_TRANSLUCENT_SHADER = new RenderStateShard.ShaderStateShard(
            () -> rendertypeHalfTranslucent
    );


	@Override
	public void onInitializeClient() {
		CoreShaderRegistrationCallback.EVENT.register(
                (CoreShaderRegistrationCallback.RegistrationContext context) -> {
                    context.register(VoidExpansion.resourceLocation("rendertype_half_translucent"), DefaultVertexFormat.BLOCK, shaderInstance -> rendertypeHalfTranslucent = shaderInstance);
                }
        );

        BlockEntityRenderers.register(VoidExpansion.SKY_CRYSTAL_BE_TYPE, SkyCrystalRenderer::new);
	}
}