package net.keepsakes.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.keepsakes.block.ModBlocks;
import net.keepsakes.helper.GameRendererPickHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.HashSet;
import java.util.Set;

// Implemented from https://github.com/Fuzss/cutthrough/blob/main/1.21.1/Common/src/main/java/fuzs/cutthrough/mixin/client/GameRendererMixin.java

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Unique
    private static final Set<Block> CUTTHROUGH$ALLOWED_BLOCKS = new HashSet<>();

    @Unique
    private static boolean cutthrough$blocksInitialized = false;

    @Unique
    private static void cutthrough$initializeAllowedBlocks() {
        if (cutthrough$blocksInitialized) return;

        CUTTHROUGH$ALLOWED_BLOCKS.add(ModBlocks.DEMATERIALIZED_BLOCK);

        cutthrough$blocksInitialized = true;
    }

    @Unique
    private static boolean cutthrough$isBlockAllowed(HitResult hitResult, Entity entity) {
        if (hitResult.getType() != HitResult.Type.BLOCK) return false;

        if (!cutthrough$blocksInitialized) {
            cutthrough$initializeAllowedBlocks();
        }

        BlockHitResult blockHit = (BlockHitResult) hitResult;
        Block block = entity.getWorld().getBlockState(blockHit.getBlockPos()).getBlock();

        return CUTTHROUGH$ALLOWED_BLOCKS.contains(block);
    }

    @ModifyVariable(
            method = "findCrosshairTarget", // This might be called "pick" in some mappings - adjust if needed
            at = @At(value = "STORE")
    )
    private HitResult pick$0(HitResult hitResult, Entity entity, double blockInteractionRange, double entityInteractionRange, float partialTick,
                             @Share("originalHitResult") LocalRef<HitResult> originalHitResult) {

        // Only run custom pick logic if we hit an allowed block or no block at all
        if (hitResult.getType() != HitResult.Type.BLOCK || cutthrough$isBlockAllowed(hitResult, entity)) {
            double pickRange = Math.max(blockInteractionRange, entityInteractionRange);
            HitResult newHitResult = GameRendererPickHelper.pick(entity, pickRange, partialTick);
            Vec3d eyePosition = entity.getEyePos();

            if (newHitResult.getPos().squaredDistanceTo(eyePosition) >
                    hitResult.getPos().squaredDistanceTo(eyePosition)) {
                originalHitResult.set(hitResult);
                return newHitResult;
            }
        }

        return hitResult;
    }

    @ModifyReturnValue(
            method = "findCrosshairTarget", // This might be called "pick" in some mappings - adjust if needed
            at = @At("TAIL")
    )
    private HitResult pick$1(HitResult hitResult, Entity entity, double blockInteractionRange, double entityInteractionRange, float partialTick,
                             @Share("originalHitResult") LocalRef<HitResult> originalHitResult) {

        if (originalHitResult.get() != null && hitResult.getType() != HitResult.Type.ENTITY) {
            if (cutthrough$isBlockAllowed(originalHitResult.get(), entity)) {
                Vec3d eyePosition = entity.getEyePos();
                if (originalHitResult.get().getPos().squaredDistanceTo(eyePosition) <
                        hitResult.getPos().squaredDistanceTo(eyePosition)) {
                    return originalHitResult.get();
                }
            }
        }

        return hitResult;
    }
}