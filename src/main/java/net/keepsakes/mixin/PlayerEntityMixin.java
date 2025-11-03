package net.keepsakes.mixin;

import net.keepsakes.item.base.CustomCriticalHitItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Unique
    private LivingEntity keepsakes$currentCritTarget;

    @Unique
    private boolean keepsakes$cancelCrit = false;

    // Capture the target and check for crit cancellation
    @Inject(method = "attack", at = @At("HEAD"))
    private void keepsakes$captureTarget(Entity target, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        this.keepsakes$currentCritTarget = target instanceof LivingEntity ? (LivingEntity) target : null;
        this.keepsakes$cancelCrit = false;

        if (keepsakes$currentCritTarget != null) {
            for (Hand hand : Hand.values()) {
                ItemStack stack = player.getStackInHand(hand);
                if (!stack.isEmpty() && stack.getItem() instanceof CustomCriticalHitItem critItem) {
                    if (critItem.shouldCancelCrit(stack, keepsakes$currentCritTarget, player)) {
                        this.keepsakes$cancelCrit = true;
                        break;
                    }
                }
            }
        }
    }

    // Prevent the critical hit boolean from being set to true
    @ModifyVariable(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;isSprinting()Z",
                    ordinal = 0
            ),
            ordinal = 0
    )
    private boolean keepsakes$cancelCritBoolean(boolean bl3) {
        if (keepsakes$cancelCrit && bl3) {
            // If we're canceling crit and this would be a crit, return false to prevent it
            return false;
        }
        return bl3;
    }

    // Trigger postCrit for successful critical hits (when not canceled)
    @Inject(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;addCritParticles(Lnet/minecraft/entity/Entity;)V"
            )
    )
    private void keepsakes$triggerPostCrit(Entity target, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        if (keepsakes$currentCritTarget != null && !keepsakes$cancelCrit) {
            for (Hand hand : Hand.values()) {
                ItemStack stack = player.getStackInHand(hand);
                if (!stack.isEmpty() && stack.getItem() instanceof CustomCriticalHitItem critItem) {
                    critItem.postCrit(stack, keepsakes$currentCritTarget, player);
                }
            }
        }
    }
}