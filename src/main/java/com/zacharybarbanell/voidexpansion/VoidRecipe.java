package com.zacharybarbanell.voidexpansion;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class VoidRecipe extends SingleItemRecipe {

    public VoidRecipe(String string, Ingredient ingredient, ItemStack itemStack) {
        super(VoidExpansion.VOID_RECIPE, VoidExpansion.VOID_RECIPE_SERIALIZER, string, ingredient, itemStack);
    }

    @Override
    public boolean matches(SingleRecipeInput recipeInput, Level level) {
        return this.ingredient.test(recipeInput.item());
    }
}
