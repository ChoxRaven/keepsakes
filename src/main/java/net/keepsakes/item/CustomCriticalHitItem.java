package net.keepsakes.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public interface CustomCriticalHitItem {
    default void postCrit(ItemStack stack, LivingEntity target,  PlayerEntity attacker) {
        // Called when a crit is landed
    }

    default boolean shouldCancelCrit(ItemStack stack, LivingEntity target, PlayerEntity attacker) {
        return false;
    }
}
