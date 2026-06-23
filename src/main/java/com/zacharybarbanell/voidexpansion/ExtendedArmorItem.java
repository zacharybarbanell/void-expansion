package com.zacharybarbanell.voidexpansion;

import com.google.common.base.Suppliers;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.List;
import java.util.function.Supplier;

public class ExtendedArmorItem extends ArmorItem {
    protected final Supplier<ItemAttributeModifiers> realDefaultModifiers;

    public ExtendedArmorItem(Holder<ArmorMaterial> holder, Type type, Properties properties, List<ItemAttributeModifiers.Entry> extraItemAttributeModifiers) {
        super(holder, type, properties);
        this.realDefaultModifiers = Suppliers.memoize(Suppliers.compose(
                itemAttributeModifiers -> {
                    for (ItemAttributeModifiers.Entry entry : extraItemAttributeModifiers) {
                        itemAttributeModifiers = itemAttributeModifiers.withModifierAdded(entry.attribute(), entry.modifier(), entry.slot());
                    }
                    return itemAttributeModifiers;
                },
                this.defaultModifiers::get
        ));
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers() {
        return this.realDefaultModifiers.get();
    }
}
