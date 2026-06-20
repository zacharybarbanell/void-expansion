package com.zacharybarbanell.voidexpansion.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zacharybarbanell.voidexpansion.VoidExpansion;
import com.zacharybarbanell.voidexpansion.VoidExpansionItems;
import com.zacharybarbanell.voidexpansion.VoidRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Entity.class)
public class EntityMixin {

    @Shadow
    public SynchedEntityData getEntityData() {
        throw new NotImplementedException();
    }

    @Shadow
    public Vec3 getDeltaMovement() {
        throw new NotImplementedException();
    }

    @Shadow
    public void addDeltaMovement(Vec3 vec3) {
        throw new NotImplementedException();
    }

    @Shadow
    public Level level() {
        throw new NotImplementedException();
    }

    @Shadow
    public Vec3 position() {
        throw new NotImplementedException();
    }

    @WrapOperation(
            method = "onBelowWorld",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;discard()V")
    )
    protected void maybePreventDiscard(Entity instance, Operation<Void> original) {
        original.call(instance);
    }

    @Mixin(ItemEntity.class)
    private static abstract class ItemEntityMixin extends EntityMixin {
        @Shadow
        public abstract ItemStack getItem();

        @Shadow
        protected abstract double getDefaultGravity();

        @Shadow
        public abstract void setItem(ItemStack itemStack);

        @Unique
        private static final EntityDataAccessor<Boolean> DATA_IS_LEVITATING = SynchedEntityData.defineId(ItemEntity.class, EntityDataSerializers.BOOLEAN);

        @Unique
        private static final String isLevitatingSaveDataKey = String.format("%s:isLevitating", VoidExpansion.MOD_ID);

        @Unique
        public boolean getIsLevitating() {
            return this.getEntityData().get(DATA_IS_LEVITATING);
        }

        @Unique
        public void setIsLevitating(boolean value) {
            this.getEntityData().set(DATA_IS_LEVITATING, value);
        }

        @Inject(method = "defineSynchedData", at = @At("RETURN"))
        private void addExtraSynchedData(SynchedEntityData.Builder builder, CallbackInfo ci) {
            builder.define(DATA_IS_LEVITATING, false);
        }

        @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
        private void addExtraAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
            compoundTag.putBoolean(isLevitatingSaveDataKey, this.getIsLevitating());
        }

        @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
        private void readExtraAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
            this.setIsLevitating(compoundTag.getBoolean(isLevitatingSaveDataKey));
        }

        @Override
        protected void maybePreventDiscard(Entity instance, Operation<Void> original) {
            if (this.getIsLevitating()) {
                if (this.getDeltaMovement().y < 0) {
                    this.addDeltaMovement(new Vec3(0, -0.1 * this.getDeltaMovement().y, 0));
                }
                return;
            }

            Optional<RecipeHolder<VoidRecipe>> recipe;

            if (this.getItem().has(VoidExpansion.VOID_IMMUNE)) {
                this.setIsLevitating(true);
            } else if (
                    (recipe = this.level().getRecipeManager().getRecipeFor(
                        VoidExpansion.VOID_RECIPE, new SingleRecipeInput(this.getItem()), this.level()
                    )).isPresent()
            ) {
                int count = this.getItem().getCount();
                this.setItem(recipe.get().value().assemble(new SingleRecipeInput(this.getItem()), null));
                this.getItem().setCount(count);
                this.setIsLevitating(true);
            } else {
                original.call(instance);
            }
        }

        @Inject(
                method = "tick",
                at = @At(
                        value = "INVOKE_ASSIGN",
                        target = "Lnet/minecraft/world/entity/item/ItemEntity;getDeltaMovement()Lnet/minecraft/world/phys/Vec3;",
                        ordinal = 0
                )
        )
        private void adjustVelocity(CallbackInfo ci) {
            if (this.getEntityData().get(DATA_IS_LEVITATING)) {
                int target = this.level().getMinBuildHeight() + 64;
                int radius = 3;
                double height = this.position().y;
                double mult;
                if (height > target + radius) {
                    mult = 0;
                } else if (height < target - radius) {
                    mult = 1;
                } else {
                    mult = Mth.smoothstep(Mth.inverseLerp(height, target + radius, target - radius));
                }
                this.addDeltaMovement(new Vec3(0, 2 * mult * this.getDefaultGravity(), 0));
            }
        }


        @ModifyReturnValue(method = "getDefaultGravity", at = @At("RETURN"))
        protected double maybeFlipGravity(double d) {
            if (this.getItem().has(VoidExpansion.FALLS_UP)) {
                return -d;
            }
            else {
                return d;
            }
        }
    }
}
