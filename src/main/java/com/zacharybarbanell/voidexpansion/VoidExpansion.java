package com.zacharybarbanell.voidexpansion;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.world.level.biome.Biomes.THE_END;

public class VoidExpansion implements ModInitializer {
	public static final String MOD_ID = "void-expansion";
    public static ResourceLocation resourceLocation(String string) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, string);
    }

    public static final TagKey<Item> FALLS_UP = TagKey.create(Registries.ITEM, resourceLocation("falls_up"));
    public static final TagKey<Item> VOID_RESISTANT = TagKey.create(Registries.ITEM, resourceLocation("void_resistance"));
	public static final RecipeSerializer<VoidRecipe> VOID_RECIPE_SERIALIZER = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            resourceLocation("void_crafting"),
            new SingleItemRecipe.Serializer<>(VoidRecipe::new)
    );
    public static final RecipeType<VoidRecipe> VOID_RECIPE = Registry.register(
            BuiltInRegistries.RECIPE_TYPE,
            resourceLocation("void_crafting"),
            new RecipeType<VoidRecipe>() {
                public String toString() {
                    return "void_crafting";
                }
            }
    );
    public static final PlacementModifierType<BelowWorldPlacement> BELOW_WORLD_PLACEMENT = Registry.register(
            BuiltInRegistries.PLACEMENT_MODIFIER_TYPE,
            resourceLocation("below_world"),
            () -> BelowWorldPlacement.CODEC
    );
    public static final Feature<OreConfiguration> TODO = Registry.register(
            BuiltInRegistries.FEATURE,
            resourceLocation("ore_exposed_below"),
            OreFeatureExposedBelow.INSTANCE
    );


    // This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
        VoidExpansionBlocks.initialize();
        VoidExpansionItems.initialize();

        BiomeModifications.addFeature(
                BiomeSelectors.foundInTheEnd().and(context -> !context.getBiomeKey().equals(THE_END)),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                ResourceKey.create(Registries.PLACED_FEATURE, resourceLocation("ore_sky_crystal"))
        );

		LOGGER.info("Hello Fabric world!");
	}
}