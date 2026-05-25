package com.zacharybarbanell.voidexpansion;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockStateMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class VoidExpansionDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(VoidExpansionItemTagProvider::new);
        pack.addProvider(VoidExpansionRecipeProvider::new);
        pack.addProvider(VoidExpansionWorldgenProvider::new);
	}

    @Override
    public void buildRegistry(RegistrySetBuilder registrySetBuilder) {
        registrySetBuilder.add(Registries.CONFIGURED_FEATURE, VoidExpansionConfiguredFeatures::configure);
        registrySetBuilder.add(Registries.PLACED_FEATURE, VoidExpansionPlacedFeatures::configure);
    }

    public static class VoidExpansionItemTagProvider extends FabricTagProvider<Item> {
        public VoidExpansionItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, Registries.ITEM, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider wrapperLookup) {
            getOrCreateTagBuilder(VoidExpansion.FALLS_UP)
                    .add(Items.IRON_INGOT)
                    .setReplace(true);
            getOrCreateTagBuilder(VoidExpansion.VOID_RESISTANT)
                    .add(Items.COPPER_INGOT)
                    .setReplace(true);
        }
    }

    public static class VoidExpansionRecipeProvider extends FabricRecipeProvider {
        public VoidExpansionRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        public void buildRecipes(RecipeOutput exporter) {
            new SingleItemRecipeBuilder(RecipeCategory.MISC, VoidRecipe::new, Ingredient.of(Items.GOLD_INGOT), Items.NETHERITE_INGOT, 1)
                    .unlockedBy(RecipeProvider.getHasName(Items.GOLD_INGOT), RecipeProvider.has(Items.GOLD_INGOT))
                    .save(exporter, RecipeProvider.getConversionRecipeName(Items.NETHERITE_INGOT, Items.GOLD_INGOT) + "_void_crafting");
        }
    }

    public static class VoidExpansionWorldgenProvider extends FabricDynamicRegistryProvider {
        public VoidExpansionWorldgenProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }


        @Override
        protected void configure(HolderLookup.Provider registries, Entries entries) {
            entries.addAll(registries.lookupOrThrow(Registries.CONFIGURED_FEATURE));
            entries.addAll(registries.lookupOrThrow(Registries.PLACED_FEATURE));
        }

        @Override
        public String getName() {
            return "Worldgen";
        }
    }

    public static class VoidExpansionConfiguredFeatures {
        public static final ResourceKey<ConfiguredFeature<?, ?>> SKY_CRYSTAL_VEIN_CONFIGURED_KEY =
                ResourceKey.create(
                        Registries.CONFIGURED_FEATURE,
                        VoidExpansion.resourceLocation("ore_sky_crystal")
                );
        public static final RuleTest endStoneReplaceableRule = new BlockStateMatchTest(Blocks.END_STONE.defaultBlockState());
        public static final List<OreConfiguration.TargetBlockState> skyCrystalOreConfig =
                List.of(
                  OreConfiguration.target(endStoneReplaceableRule, VoidExpansionBlocks.SKY_CRYSTAL.defaultBlockState())
                );

        public static void configure(BootstrapContext<ConfiguredFeature<?, ?>> bootstrapContext) {
            bootstrapContext.register(SKY_CRYSTAL_VEIN_CONFIGURED_KEY, new ConfiguredFeature<>(
                    OreFeatureExposedBelow.INSTANCE,
                    new OreConfiguration(skyCrystalOreConfig, 24, 1.0F)
            ));
        }
    }

    public static class VoidExpansionPlacedFeatures {
        public static final ResourceKey<PlacedFeature> SKY_CRYSTAL_VEIN_PLACED_KEY =
                ResourceKey.create(
                        Registries.PLACED_FEATURE,
                        VoidExpansion.resourceLocation("ore_sky_crystal")
                );

        public static void configure(BootstrapContext<PlacedFeature> bootstrapContext) {
            HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = bootstrapContext.lookup(Registries.CONFIGURED_FEATURE);
            bootstrapContext.register(
                    SKY_CRYSTAL_VEIN_PLACED_KEY,
                    new PlacedFeature(
                            configuredFeatures.getOrThrow(VoidExpansionConfiguredFeatures.SKY_CRYSTAL_VEIN_CONFIGURED_KEY),
                            List.of(
                                    RarityFilter.onAverageOnceEvery(30),
                                    InSquarePlacement.spread(),
                                    BiomeFilter.biome(),
                                    BelowWorldPlacement.placement()
                            )
                    )
            );
        }
    }
}
