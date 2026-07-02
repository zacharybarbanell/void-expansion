package com.zacharybarbanell.voidexpansion;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.*;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.*;
import net.minecraft.data.recipes.*;
import net.minecraft.data.recipes.packs.VanillaRecipeProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
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
        pack.addProvider(VoidExpansionLootTables.VoidExpansionBlockLootTableProvider::new);
        pack.addProvider(VoidExpansionLootTables.VoidExpansionChestLootTableProvider::new);
        pack.addProvider(VoidExpansionItemTagProvider::new);
        pack.addProvider(VoidExpansionBlockTagProvider::new);
        pack.addProvider(VoidExpansionRecipeProvider::new);
        pack.addProvider(VoidExpansionModelProvider::new);
        pack.addProvider(VoidExpansionWorldgenProvider::new);
        pack.addProvider(VoidExpansionLangProviders.VoidExpansionEnglishLangProvider::new);
	}

    @Override
    public void buildRegistry(RegistrySetBuilder registrySetBuilder) {
        registrySetBuilder.add(Registries.CONFIGURED_FEATURE, VoidExpansionFeatures.ConfiguredFeatures::configure);
        registrySetBuilder.add(Registries.PLACED_FEATURE, VoidExpansionFeatures.PlacedFeatures::configure);
    }

    public static class VoidExpansionItemTagProvider extends FabricTagProvider<Item> {
        public VoidExpansionItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, Registries.ITEM, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider wrapperLookup) {
            getOrCreateTagBuilder(ItemTags.HEAD_ARMOR)
                    .add(VoidExpansionItems.VOID_HELMET)
                    .setReplace(false);
            getOrCreateTagBuilder(ItemTags.CHEST_ARMOR)
                    .add(VoidExpansionItems.VOID_CHESTPLATE)
                    .setReplace(false);
            getOrCreateTagBuilder(ItemTags.LEG_ARMOR)
                    .add(VoidExpansionItems.VOID_LEGGINGS)
                    .setReplace(false);
            getOrCreateTagBuilder(ItemTags.FOOT_ARMOR)
                    .add(VoidExpansionItems.VOID_BOOTS)
                    .setReplace(false);
        }
    }

    public static class VoidExpansionBlockTagProvider extends FabricTagProvider<Block> {
        public VoidExpansionBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, Registries.BLOCK, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider wrapperLookup) {
            getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
                    .add(VoidExpansionBlocks.SKY_CRYSTAL)
                    .add(VoidExpansionBlocks.VOID_BLOCK)
                    .setReplace(false);
            getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
                    .add(VoidExpansionBlocks.VOID_BLOCK)
                    .setReplace(false);
        }
    }

    public static class VoidExpansionRecipeProvider extends FabricRecipeProvider {
        public VoidExpansionRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        public void buildRecipes(RecipeOutput recipeOutput) {
            new SingleItemRecipeBuilder(RecipeCategory.MISC, VoidRecipe::new, Ingredient.of(VoidExpansionItems.ENCRUSTED_NUGGET), VoidExpansionItems.VOID_NUGGET, 1)
                    .unlockedBy(RecipeProvider.getHasName(VoidExpansionItems.ENCRUSTED_NUGGET), RecipeProvider.has(VoidExpansionItems.ENCRUSTED_NUGGET))
                    .save(recipeOutput, RecipeProvider.getConversionRecipeName(VoidExpansionItems.VOID_NUGGET, VoidExpansionItems.ENCRUSTED_NUGGET) + "_void_crafting");

            VanillaRecipeProvider.oreSmelting(
                    recipeOutput,
                    List.of(VoidExpansionBlocks.SKY_CRYSTAL),
                    RecipeCategory.MISC,
                    VoidExpansionItems.SKY_SHARD,
                    1.5F,
                    200,
                    "sky_shard"
            );
            VanillaRecipeProvider.oreBlasting(
                    recipeOutput,
                    List.of(VoidExpansionBlocks.SKY_CRYSTAL),
                    RecipeCategory.MISC,
                    VoidExpansionItems.SKY_SHARD,
                    1.5F,
                    100,
                    "sky_shard"
            );

            VanillaRecipeProvider.nineBlockStorageRecipesRecipesWithCustomUnpacking(
                    recipeOutput, RecipeCategory.MISC, VoidExpansionItems.VOID_INGOT, RecipeCategory.BUILDING_BLOCKS, VoidExpansionBlocks.VOID_BLOCK.asItem(), "void_ingot_from_void_block", "void_ingot"
            );

            VanillaRecipeProvider.nineBlockStorageRecipesWithCustomPacking(
                    recipeOutput, RecipeCategory.MISC, VoidExpansionItems.VOID_NUGGET, RecipeCategory.MISC, VoidExpansionItems.VOID_INGOT, "void_ingot_from_nuggets", "void_ingot"
            );

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, VoidExpansionItems.ENCRUSTED_NUGGET)
                    .define('#', VoidExpansionItems.SKY_SHARD)
                    .define('X', Items.IRON_NUGGET)
                    .pattern("###")
                    .pattern("#X#")
                    .pattern("###")
                    .unlockedBy("has_sky_shard", has(VoidExpansionItems.SKY_SHARD))
                    .save(recipeOutput);

            //TODO replace with real copy material
            VanillaRecipeProvider.copySmithingTemplate(recipeOutput, VoidExpansionItems.VOID_UPGRADE_SMITHING_TEMPLATE, Items.BARRIER);

            voidSmithingRecipe(recipeOutput, RecipeCategory.COMBAT, Ingredient.of(Items.DIAMOND_HELMET), VoidExpansionItems.VOID_HELMET);
            voidSmithingRecipe(recipeOutput, RecipeCategory.COMBAT, Ingredient.of(Items.DIAMOND_CHESTPLATE), VoidExpansionItems.VOID_CHESTPLATE);
            voidSmithingRecipe(recipeOutput, RecipeCategory.COMBAT, Ingredient.of(Items.DIAMOND_LEGGINGS), VoidExpansionItems.VOID_LEGGINGS);
            voidSmithingRecipe(recipeOutput, RecipeCategory.COMBAT, Ingredient.of(Items.DIAMOND_BOOTS), VoidExpansionItems.VOID_BOOTS);
        }

        private static void voidSmithingRecipe(RecipeOutput recipeOutput, RecipeCategory category, Ingredient input, Item output) {
            SmithingTransformRecipeBuilder.smithing(
                            Ingredient.of(VoidExpansionItems.VOID_UPGRADE_SMITHING_TEMPLATE),
                            input,
                            Ingredient.of(VoidExpansionItems.VOID_INGOT),
                            category,
                            output
                    )
                    .unlocks("has_void_ingot", RecipeProvider.has(VoidExpansionItems.VOID_INGOT))
                    .save(recipeOutput, RecipeProvider.getItemName(output) + "_smithing");
        }
    }

    public static class VoidExpansionModelProvider extends FabricModelProvider {
        public VoidExpansionModelProvider(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
            blockStateModelGenerator.createTrivialCube(VoidExpansionBlocks.SKY_CRYSTAL);
            blockStateModelGenerator.createTrivialCube(VoidExpansionBlocks.VOID_BLOCK);
            blockStateModelGenerator.createTrivialBlock(VoidExpansionBlocks.ANTIGRAVITY_PROJECTOR, TexturedModel.CUBE_TOP_BOTTOM);
            blockStateModelGenerator.createTrivialBlock(VoidExpansionBlocks.ANTIGRAVITY_BEAM, TexturedModel.createDefault(
                    block -> new TextureMapping().put(TextureSlot.PARTICLE, MissingTextureAtlasSprite.getLocation()),
                    ModelTemplates.PARTICLE_ONLY
            ));
        }

        @Override
        public void generateItemModels(ItemModelGenerators itemModelGenerator) {
            itemModelGenerator.generateFlatItem(VoidExpansionItems.SKY_SHARD, ModelTemplates.FLAT_ITEM);
            itemModelGenerator.generateFlatItem(VoidExpansionItems.ENCRUSTED_NUGGET, ModelTemplates.FLAT_ITEM);
            itemModelGenerator.generateFlatItem(VoidExpansionItems.VOID_NUGGET, ModelTemplates.FLAT_ITEM);
            itemModelGenerator.generateFlatItem(VoidExpansionItems.VOID_INGOT, ModelTemplates.FLAT_ITEM);

            itemModelGenerator.generateFlatItem(VoidExpansionItems.VOID_UPGRADE_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);

            itemModelGenerator.generateArmorTrims(VoidExpansionItems.VOID_HELMET);
            itemModelGenerator.generateArmorTrims(VoidExpansionItems.VOID_CHESTPLATE);
            itemModelGenerator.generateArmorTrims(VoidExpansionItems.VOID_LEGGINGS);
            itemModelGenerator.generateArmorTrims(VoidExpansionItems.VOID_BOOTS);
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
}
