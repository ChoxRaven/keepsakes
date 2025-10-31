package net.keepsakes.mixin;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.keepsakes.item.CustomPrimaryUseItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.CustomPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class ClientPlayerEntityMixin {
    @Shadow
    public ClientPlayerEntity player;

    // Intercept attack (left-click on entity or air)
    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void keepsakes$cancelAttacking(CallbackInfoReturnable<Boolean> cir) {
        if (player != null) {
            ItemStack mainHandStack = player.getMainHandStack();
            if (!mainHandStack.isEmpty() && mainHandStack.getItem() instanceof CustomPrimaryUseItem customPrimaryUseItem) {
                CustomPayload payload = customPrimaryUseItem.getPrimaryUsePayload();
                boolean shouldCancelEntityAttacking = customPrimaryUseItem.shouldCancelEntityAttacking();

                if (payload != null) {
                    ClientPlayNetworking.send(payload);
                }

                if (shouldCancelEntityAttacking) {
                    cir.setReturnValue(false); // Cancel the attack
                }
            }
        }
    }

    // Intercept block breaking (left-click on block)
    @Inject(method = "handleBlockBreaking", at = @At("HEAD"), cancellable = true)
    private void keepsakes$cancelBlockBreaking(boolean breaking, CallbackInfo ci) {
        if (breaking && player != null) {
            ItemStack mainHandStack = player.getMainHandStack();
            if (!mainHandStack.isEmpty() && mainHandStack.getItem() instanceof CustomPrimaryUseItem customPrimaryUseItem) {
                boolean shouldCancelBlockBreaking = customPrimaryUseItem.shouldCancelBlockBreaking();

                if (shouldCancelBlockBreaking) {
                    ci.cancel(); // Cancel block breaking
                }
            }
        }
    }
}