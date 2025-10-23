package net.keepsakes.mixin;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.keepsakes.Keepsakes;
import net.keepsakes.item.ModItems;
import net.keepsakes.item.custom.Dematerializer;
import net.keepsakes.networking.packet.DematerializerLeftClickPayload;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;

@Mixin(MinecraftClient.class)
public class DisableLeftClickMixin {
    @Shadow
    public ClientPlayerEntity player;

    @Unique
    private static final Set<Item> DISABLED_LEFT_CLICK_ITEMS = new HashSet<>();

    static {
        // Register items that should have left click disabled
        DISABLED_LEFT_CLICK_ITEMS.add(ModItems.DEMATERIALIZER);
    }

    // Intercept attack (left-click on entity or air)
    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void onDoAttack(CallbackInfoReturnable<Boolean> cir) {
        if (player != null) {
            ItemStack mainHandStack = player.getMainHandStack();
            if (!mainHandStack.isEmpty() && DISABLED_LEFT_CLICK_ITEMS.contains(mainHandStack.getItem())) {
                // Calls the custom function on the Dematerializer
                Keepsakes.LOGGER.info("CLIENT: Sending DematerializerLeftClickPayload");
                ClientPlayNetworking.send(new DematerializerLeftClickPayload());

                cir.setReturnValue(false); // Cancel the attack
            }
        }
    }

    // Intercept block breaking (left-click on block)
    @Inject(method = "handleBlockBreaking", at = @At("HEAD"), cancellable = true)
    private void onHandleBlockBreaking(boolean breaking, CallbackInfo ci) {
        if (breaking && player != null) {
            ItemStack mainHandStack = player.getMainHandStack();
            if (!mainHandStack.isEmpty() && DISABLED_LEFT_CLICK_ITEMS.contains(mainHandStack.getItem())) {
                ci.cancel(); // Cancel block breaking
            }
        }
    }
}