package com.zacharybarbanell.voidexpansion;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.resources.model.ModelResourceLocation;

public class VoidExpansionModelLoadingPlugin implements ModelLoadingPlugin {
    public static final ModelResourceLocation SKY_CRYSTAL_MODEL = new ModelResourceLocation(VoidExpansion.resourceLocation("sky_crystal"), "");
    public static final ModelResourceLocation SKY_CRYSTAL_MODEL_ITEM = new ModelResourceLocation(VoidExpansion.resourceLocation("sky_crystal"), "inventory");

    @Override
    public void onInitializeModelLoader(Context context) {
        context.modifyModelOnLoad().register((original,ctxt) -> {
                final ModelResourceLocation id = ctxt.topLevelId();
                if (id != null && (id.equals(SKY_CRYSTAL_MODEL) || id.equals(SKY_CRYSTAL_MODEL_ITEM))) {
                    VoidExpansion.LOGGER.error("BRIGHT RED TEXT");
                    return new SkyCrystalModel();
                }
                else {
                    return original;
                }
            }
        );
    }
}
