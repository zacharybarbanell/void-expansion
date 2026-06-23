package com.zacharybarbanell.voidexpansion;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class VoidExpansionLootTables {
    public static ResourceKey<LootTable> END_CITY_TREASURE_ADDITIONAL = ResourceKey.create(
            Registries.LOOT_TABLE,
            VoidExpansion.resourceLocation("chests/end_city_treasure_additional")
    );

    public static class VoidExpansionBlockLootTableProvider extends FabricBlockLootTableProvider {
        public VoidExpansionBlockLootTableProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        public void generate() {
            dropSelf(VoidExpansionBlocks.VOID_BLOCK);
            add(VoidExpansionBlocks.SKY_CRYSTAL, createOreDrop(VoidExpansionBlocks.SKY_CRYSTAL, VoidExpansionItems.SKY_SHARD));
        }
    }

    public static class VoidExpansionChestLootTableProvider extends SimpleFabricLootTableProvider {
        public VoidExpansionChestLootTableProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
            super(output, registryLookup, LootContextParamSets.CHEST);
        }

        @Override
        public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer) {
            biConsumer.accept(
                    END_CITY_TREASURE_ADDITIONAL,
                    LootTable.lootTable()
                            .withPool(
                                    LootPool.lootPool()
                                            .setRolls(ConstantValue.exactly(1.0F))
                                            .add(LootItem.lootTableItem(VoidExpansionItems.VOID_UPGRADE_SMITHING_TEMPLATE).setWeight(1))
                                            .add(EmptyLootItem.emptyItem().setWeight(9))
                            )
            );
        }
    }
}
