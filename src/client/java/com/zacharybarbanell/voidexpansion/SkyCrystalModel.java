package com.zacharybarbanell.voidexpansion;

import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.impl.renderer.VanillaModelEncoder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class SkyCrystalModel implements UnbakedModel, BakedModel, FabricBakedModel {

    private Mesh mesh;

    private static final Material[] MATERIALS = new Material[] {
            new Material(TextureAtlas.LOCATION_BLOCKS, VoidExpansion.resourceLocation("block/sky_crystal_outer")),
            new Material(TextureAtlas.LOCATION_BLOCKS, VoidExpansion.resourceLocation("block/amethyst_block")), //TODO replace
    };
    private final TextureAtlasSprite[] sprites = new TextureAtlasSprite[MATERIALS.length];

    private static final int MATERIAL_OUTER = 0;
    private static final int MATERIAL_INNER = 1;

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, RandomSource randomSource) {
        VoidExpansion.LOGGER.warn("SkyCrystalModel getQuads called"); //This should be unused
        return List.of();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
            return sprites[MATERIAL_OUTER]; //TODO
    }

    @Override
    public ItemTransforms getTransforms() {
        return ModelHelper.MODEL_TRANSFORM_BLOCK;
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return List.of();
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> function) {

    }

    @Override
    public @Nullable BakedModel bake(ModelBaker modelBaker, Function<Material, TextureAtlasSprite> textureGetter, ModelState modelState) {
        for(int i = 0; i < MATERIALS.length; ++i) {
            sprites[i] = textureGetter.apply(MATERIALS[i]);
        }

        Renderer renderer = RendererAccess.INSTANCE.getRenderer();
        MeshBuilder builder = renderer.meshBuilder();
        QuadEmitter emitter = builder.getEmitter();

        for(Direction direction : Direction.values()) {
            emitter.square(direction, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F);
            emitter.spriteBake(sprites[MATERIAL_OUTER], MutableQuadView.BAKE_LOCK_UV);
            emitter.color(-1,-1,-1,-1);
            emitter.emit();
        }

        Vector3f[] offsets = new Vector3f[] {
                new Vector3f(1.0F, 0.0F, 1.0F),
                new Vector3f(1.0F, 0.0F, -1.0F),
                new Vector3f(-1.0F, 0.0F, -1.0F),
                new Vector3f(-1.0F, 0.0F, 1.0F)
        };

        Vector3f up = new Vector3f(0.0F, 1.0F, 0.0F);
        Vector3f down = new Vector3f(0.0F, -1.0F, 0.0F);
        Vector3f center = new Vector3f(0.5F, 0.5F, 0.5F);

        float radius = (float) (8.0D/(16.0D * Math.sqrt(3)));



        for(int i = 0; i < 4; ++i) {
            emitter.cullFace(null);

            emitter.pos(0,  new Vector3f(center).add(new Vector3f(up).mul(radius)));
            emitter.pos(1, new Vector3f(center).add(new Vector3f(offsets[i]).mul(radius)));
            emitter.pos(2, new Vector3f(center).add(new Vector3f(offsets[i]).add(offsets[(i+1)%4]).mul(radius/2.0F)));
            emitter.pos(3, new Vector3f(center).add(new Vector3f(offsets[(i+1)%4]).mul(radius)));

            TextureAtlasSprite sprite = sprites[MATERIAL_INNER];

            float uLow = sprite.getU0();
            float uSize = sprite.getU1() - uLow;
            float vLow = sprite.getV0();
            float vSize = sprite.getV1() - vLow;

            emitter.uv(0, uLow, vLow);
            emitter.uv(1, uLow + uSize, vLow);
            emitter.uv(2, uLow + uSize / 2.0F, vLow + vSize / 2.0F);
            emitter.uv(3, uLow, vLow + vSize);

            VoidExpansion.LOGGER.error("{}: {}", i, emitter.faceNormal());

            emitter.color(-1,-1,-1,-1);
            emitter.emit();
        }

        mesh = builder.build();

        return this;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        mesh.outputTo(context.getEmitter());
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
        mesh.outputTo(context.getEmitter());
    }

}
