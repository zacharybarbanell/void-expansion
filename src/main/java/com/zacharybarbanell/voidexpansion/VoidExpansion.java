package com.zacharybarbanell.voidexpansion;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static net.minecraft.world.level.biome.Biomes.THE_END;

public class VoidExpansion implements ModInitializer {
	public static final String MOD_ID = "void-expansion";
    public static ResourceLocation resourceLocation(String string) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, string);
    }

    public static DataComponentType<Unit> FALLS_UP = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            VoidExpansion.resourceLocation("falls_up"),
            DataComponentType.<Unit>builder().persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)).build()
    );
    public static DataComponentType<Unit> VOID_IMMUNE = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            VoidExpansion.resourceLocation("void_immune"),
            DataComponentType.<Unit>builder().persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)).build()
    );

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

    public static final Holder<ArmorMaterial> VOID_ARMOR_MATERIAL = Registry.registerForHolder(
            BuiltInRegistries.ARMOR_MATERIAL,
            resourceLocation("void"),
            new ArmorMaterial(
                    new EnumMap<>(
                            Map.of(
                                    ArmorItem.Type.BOOTS, 3,
                                    ArmorItem.Type.LEGGINGS, 6,
                                    ArmorItem.Type.CHESTPLATE, 8,
                                    ArmorItem.Type.HELMET, 3,
                                    ArmorItem.Type.BODY, 11
                            )
                    ),
                    20, //Enchantability
                    SoundEvents.ARMOR_EQUIP_DIAMOND, //TODO replace
                    () -> Ingredient.of(VoidExpansionItems.VOID_INGOT),
                    List.of(new ArmorMaterial.Layer(resourceLocation("void"))),
                    2.0F, //Toughness
                    0.0F //Knockback Resistance
            )
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
                VoidExpansionFeatures.PlacedFeatures.SKY_CRYSTAL_VEIN_PLACED_KEY
        );

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
                    if (BuiltInLootTables.END_CITY_TREASURE == key && source.isBuiltin()) {
                        tableBuilder.withPool(
                                LootPool.lootPool()
                                        .add(NestedLootTable.lootTableReference(VoidExpansionLootTables.END_CITY_TREASURE_ADDITIONAL).setWeight(1))
                        );
                    }
                }
        );

		LOGGER.info("Hello Fabric world!");
	}
}