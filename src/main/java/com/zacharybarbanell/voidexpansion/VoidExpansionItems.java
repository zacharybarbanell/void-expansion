package com.zacharybarbanell.voidexpansion;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.List;

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

    public static final Item VOID_UPGRADE_SMITHING_TEMPLATE = register(
            new SmithingTemplateItem(
                    VoidExpansionTextComponents.VOID_UPGRADE_APPLIES_TO,
                    VoidExpansionTextComponents.VOID_UPGRADE_INGREDIENTS,
                    VoidExpansionTextComponents.VOID_UPGRADE,
                    VoidExpansionTextComponents.VOID_UPGRADE_BASE_SLOT_DESCRIPTION,
                    VoidExpansionTextComponents.VOID_UPGRADE_ADDITIONS_SLOT_DESCRIPTION,
                    List.of(
                            SmithingTemplateItem.EMPTY_SLOT_HELMET,
                            SmithingTemplateItem.EMPTY_SLOT_CHESTPLATE,
                            SmithingTemplateItem.EMPTY_SLOT_LEGGINGS,
                            SmithingTemplateItem.EMPTY_SLOT_BOOTS
                    ),
                    List.of(
                            SmithingTemplateItem.EMPTY_SLOT_INGOT
                    )
            ),
            "void_upgrade_smithing_template"
    );

    public static final Item VOID_HELMET = registerVoidArmorItem(ArmorItem.Type.HELMET, "void_helmet");
    public static final Item VOID_CHESTPLATE = registerVoidArmorItem(ArmorItem.Type.CHESTPLATE, "void_chestplate");
    public static final Item VOID_LEGGINGS = registerVoidArmorItem(ArmorItem.Type.LEGGINGS, "void_leggings");
    public static final Item VOID_BOOTS = registerVoidArmorItem(ArmorItem.Type.BOOTS, "void_boots");

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

    public static Item registerVoidArmorItem(ArmorItem.Type armorType, String id) {
        return register(
                new ExtendedArmorItem(VoidExpansion.VOID_ARMOR_MATERIAL, armorType, new Item.Properties()
                        .component(VoidExpansion.VOID_IMMUNE, Unit.INSTANCE)
                        .durability(armorType.getDurability(33)),
                        List.of(
                                new ItemAttributeModifiers.Entry(
                                        Attributes.MOVEMENT_SPEED,
                                        new AttributeModifier(
                                                VoidExpansion.resourceLocation(id + "_base_movement_speed"),
                                                0.005D,
                                                AttributeModifier.Operation.ADD_VALUE
                                        ),
                                        EquipmentSlotGroup.bySlot(armorType.getSlot())
                                ),
                                new ItemAttributeModifiers.Entry(
                                        Attributes.STEP_HEIGHT,
                                        new AttributeModifier(
                                                VoidExpansion.resourceLocation(id + "_base_step_height"),
                                                0.125D,
                                                AttributeModifier.Operation.ADD_VALUE
                                        ),
                                        EquipmentSlotGroup.bySlot(armorType.getSlot())
                                ),

                                new ItemAttributeModifiers.Entry(
                                        Attributes.SAFE_FALL_DISTANCE,
                                        new AttributeModifier(
                                                VoidExpansion.resourceLocation(id + "_base_fall_distance"),
                                                2.0D,
                                                AttributeModifier.Operation.ADD_VALUE
                                        ),
                                        EquipmentSlotGroup.bySlot(armorType.getSlot())
                                ),
                                new ItemAttributeModifiers.Entry(
                                        Attributes.GRAVITY,
                                        new AttributeModifier(
                                                VoidExpansion.resourceLocation(id + "_base_gravity"),
                                                -0.01D,
                                                AttributeModifier.Operation.ADD_VALUE
                                        ),
                                        EquipmentSlotGroup.bySlot(armorType.getSlot())
                                )
                        )
                ),
                id
        );
    }
}
