package net.keepsakes.item.base;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface CustomPrimaryUseItem {
    @Nullable
    CustomPayload getPrimaryUsePayload();

    default void primaryUse(World world, PlayerEntity user, Hand hand) {
        // Called once on the client when the player presses primary use
    }

    default void primaryUseHeld(World world, PlayerEntity user, Hand hand) {
        // Called every tick on the client while the player holds primary use
    }

    default boolean shouldCancelEntityAttacking() {
        return true;
    }

    default boolean shouldCancelBlockBreaking() {
        return true;
    }
}
