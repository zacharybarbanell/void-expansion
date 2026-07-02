package com.zacharybarbanell.voidexpansion;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.particle.EndRodParticle;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import org.jetbrains.annotations.Nullable;

public class VoidExpansionClient implements ClientModInitializer {


	@Override
	public void onInitializeClient() {
		BlockRenderLayerMap.INSTANCE.putBlock(VoidExpansionBlocks.SKY_CRYSTAL, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(VoidExpansionBlocks.ANTIGRAVITY_BEAM, RenderType.translucent());

		ParticleFactoryRegistry.getInstance().register(VoidExpansion.ANTIGRAVITY_PARTICLE, EndRodParticle.Provider::new);
	}
}