package com.zacharybarbanell.voidexpansion;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import org.jetbrains.annotations.Nullable;

public class VoidExpansionClient implements ClientModInitializer {
    

	@Override
	public void onInitializeClient() {
		ModelLoadingPlugin.register(new VoidExpansionModelLoadingPlugin());

		BlockRenderLayerMap.INSTANCE.putBlock(VoidExpansionBlocks.SKY_CRYSTAL, RenderType.translucent());
	}
}