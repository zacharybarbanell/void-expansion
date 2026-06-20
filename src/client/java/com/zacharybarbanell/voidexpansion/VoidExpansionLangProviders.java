package com.zacharybarbanell.voidexpansion;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;

import java.util.concurrent.CompletableFuture;

public class VoidExpansionLangProviders {
    public static class VoidExpansionEnglishLangProvider extends FabricLanguageProvider {
        protected VoidExpansionEnglishLangProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
            super(dataOutput, "en_us", registryLookup);
        }

        @Override
        public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
            translationBuilder.add(VoidExpansionBlocks.SKY_CRYSTAL.getDescriptionId(), "Void Crystal");
            translationBuilder.add(VoidExpansionBlocks.VOID_BLOCK.getDescriptionId(), "Voidmetal Block");

            translationBuilder.add(VoidExpansionItems.ENCRUSTED_NUGGET.getDescriptionId(), "Encrusted Nugget");
            translationBuilder.add(VoidExpansionItems.SKY_SHARD.getDescriptionId(), "Void Shard");
            translationBuilder.add(VoidExpansionItems.VOID_INGOT.getDescriptionId(), "Voidmetal Ingot");
            translationBuilder.add(VoidExpansionItems.VOID_NUGGET.getDescriptionId(), "Voidmetal Nugget");
        }
    }
}
