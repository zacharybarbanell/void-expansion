package com.zacharybarbanell.voidexpansion;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.concurrent.CompletableFuture;

public class VoidExpansionDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(VoidExpansionItemTagProvider::new);
        pack.addProvider(VoidExpansionRecipeProvider::new);
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
}
