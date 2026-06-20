package com.zacharybarbanell.voidexpansion;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;

public class VoidExpansionItems {
    public static final Item SKY_SHARD = register(
            new Item(new Item.Properties().component(VoidExpansion.FALLS_UP, Unit.INSTANCE)),
            "sky_shard"
    );
    public static final Item ENCRUSTED_NUGGET = register(
            new Item(new Item.Properties()),
            "encrusted_nugget"
    );
    public static final Item VOID_NUGGET = register(
            new Item(new Item.Properties().component(VoidExpansion.VOID_IMMUNE, Unit.INSTANCE)),
            "void_nugget"
    );
    public static final Item VOID_INGOT = register(
            new Item(new Item.Properties().component(VoidExpansion.VOID_IMMUNE, Unit.INSTANCE)),
            "void_ingot"
    );


    public static void initialize() {
    }


    public static Item register(Item item, String id) {
        // Create the identifier for the item.
        ResourceLocation itemID = VoidExpansion.resourceLocation(id);

        // Register the item.
        Item registeredItem = Registry.register(BuiltInRegistries.ITEM, itemID, item);

        // Return the registered item!
        return registeredItem;
    }
}
