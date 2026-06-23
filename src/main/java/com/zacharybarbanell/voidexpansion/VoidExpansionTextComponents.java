package com.zacharybarbanell.voidexpansion;


import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.SmithingTemplateItem;

public class VoidExpansionTextComponents {
    public static final Component VOID_UPGRADE = Component.translatable(
                    Util.makeDescriptionId("upgrade", VoidExpansion.resourceLocation("void_upgrade"))
            )
            .withStyle(SmithingTemplateItem.TITLE_FORMAT);

    public static final Component VOID_UPGRADE_APPLIES_TO = Component.translatable(
            Util.makeDescriptionId("item", VoidExpansion.resourceLocation("smithing_template.void_upgrade.applies_to"))
    )
            .withStyle(SmithingTemplateItem.DESCRIPTION_FORMAT);
    public static final Component VOID_UPGRADE_INGREDIENTS = Component.translatable(
                    Util.makeDescriptionId("item", VoidExpansion.resourceLocation("smithing_template.void_upgrade.ingredients"))
            )
            .withStyle(SmithingTemplateItem.DESCRIPTION_FORMAT);
    public static final Component VOID_UPGRADE_BASE_SLOT_DESCRIPTION = Component.translatable(
            Util.makeDescriptionId("item", VoidExpansion.resourceLocation("smithing_template.void_upgrade.base_slot_description"))
    );
    public static final Component VOID_UPGRADE_ADDITIONS_SLOT_DESCRIPTION = Component.translatable(
            Util.makeDescriptionId("item", VoidExpansion.resourceLocation("smithing_template.void_upgrade.additions_slot_description"))
    );
}
