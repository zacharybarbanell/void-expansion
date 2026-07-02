package com.zacharybarbanell.voidexpansion.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zacharybarbanell.voidexpansion.AntigravityBeamBlock;
import com.zacharybarbanell.voidexpansion.VoidExpansion;
import com.zacharybarbanell.voidexpansion.VoidRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(Entity.class)
public abstract class EntityMixin implements AntigravityBeamBlock.EnableAntigravity {

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

    @Shadow
    public static Vec3 collideBoundingBox(@Nullable Entity entity, Vec3 vec3, AABB aABB, Level level, List<VoxelShape> list) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Shadow
    public abstract AABB getBoundingBox();

    @Shadow
    public abstract double getY();

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

    @Unique
    private boolean isAntigravity = false; //Intentionally not synced

    @Inject(method = "checkInsideBlocks()V", at = @At("HEAD"))
    private void clearAntigravity(CallbackInfo ci) {
        isAntigravity = false;
    }

    @Override
    public boolean void_expansion$getAntigravity() {
        return isAntigravity;
    }

    @Override
    public void void_expansion$enableAntigravity() {
        isAntigravity = true;
    }

    @ModifyReturnValue(method = "getGravity", at = @At("RETURN"))
    protected double applyAntigravity(double d) {
        return isAntigravity ? 0 : d;
    }

    @ModifyArg(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setOnGroundWithMovement(ZLnet/minecraft/world/phys/Vec3;)V"))
    private boolean maybeOverwriteOnGround(boolean bl) {
        Vec3 down = new Vec3(0.0D, -1E-7D, 0.0D);
        AABB aabb = getBoundingBox();
        return bl ||
                (isAntigravity &&
                        collideBoundingBox((Entity) (Object) this, down, aabb, level(), level().getEntityCollisions((Entity) (Object) this, aabb.expandTowards(down))).y == 0.0D
                );
    }

    @Mixin(FlyingMob.class)
    private static abstract class FlyingMobFrictionMixin extends EntityMixin {
        @ModifyConstant(method = "travel", constant = @Constant(floatValue = 0.91F), require = 4)
        private float replaceBaseFriction(float constant) {
            return void_expansion$getAntigravity() ? 1 - (0.01f * (1 - constant)) : constant;
        }

        @Definition(id = "getFriction", method = "Lnet/minecraft/world/level/block/Block;getFriction()F")
        @Expression("?.getFriction()")
        @ModifyExpressionValue(method = "travel", at = @At("MIXINEXTRAS:EXPRESSION"), require = 2)
        private float ignoreBlockFriction(float original) {
            return void_expansion$getAntigravity() ? 1 : original;
        }
    }

    @Mixin(LivingEntity.class)
    private static abstract class LivingEntityFrictionMixin extends EntityMixin {
        @ModifyConstant(method = "travel", constant = @Constant(floatValue = 0.91F), require = 2)
        private float replaceBaseFriction(float constant) {
            return void_expansion$getAntigravity() ? 1 - (0.01f * (1 - constant)) : constant;
        }

        @Definition(id = "getFriction", method = "Lnet/minecraft/world/level/block/Block;getFriction()F")
        @Expression("?.getFriction()")
        @ModifyExpressionValue(method = "travel", at = @At("MIXINEXTRAS:EXPRESSION"), require = 1)
        private float ignoreBlockFriction(float original) {
            return void_expansion$getAntigravity() ? 1 : original;
        }
    }

    @Mixin(ItemEntity.class)
    private static abstract class ItemEntityFrictionMixin extends EntityMixin {
        @ModifyConstant(method = "tick", constant = @Constant(floatValue = 0.98F), require = 2)
        private float replaceBaseFriction(float constant) {
            return void_expansion$getAntigravity() ? 1 - (0.01f * (1 - constant)) : constant;
        }

        @ModifyConstant(method = "tick", constant = @Constant(doubleValue = 0.98D), require = 1)
        private double replaceBaseFriction(double constant) {
            return void_expansion$getAntigravity() ? 1 - (0.01D * (1 - constant)) : constant;
        }

        @Definition(id = "getFriction", method = "Lnet/minecraft/world/level/block/Block;getFriction()F")
        @Expression("?.getFriction()")
        @ModifyExpressionValue(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"), require = 1)
        private float ignoreBlockFriction(float original) {
            return void_expansion$getAntigravity() ? 1 : original;
        }
    }

    @Mixin(ExperienceOrb.class)
    private static abstract class ExperienceOrbFrictionMixin extends EntityMixin {
        @ModifyConstant(method = "tick", constant = @Constant(floatValue = 0.98F), require = 2)
        private float replaceBaseFriction(float constant) {
            return void_expansion$getAntigravity() ? 1 - (0.01f * (1 - constant)) : constant;
        }

        @ModifyConstant(method = "tick", constant = @Constant(doubleValue = 0.98D), require = 1)
        private double replaceBaseFriction(double constant) {
            return void_expansion$getAntigravity() ? 1 - (0.01D * (1 - constant)) : constant;
        }

        @Definition(id = "getFriction", method = "Lnet/minecraft/world/level/block/Block;getFriction()F")
        @Expression("?.getFriction()")
        @ModifyExpressionValue(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"), require = 1)
        private float ignoreBlockFriction(float original) {
            return void_expansion$getAntigravity() ? 1 : original;
        }
    }
}
