package net.keepsakes.item.base;

import net.minecraft.network.packet.CustomPayload;
import org.jetbrains.annotations.Nullable;

public interface CustomPrimaryUseItem {
    @Nullable
    CustomPayload getPrimaryUsePayload();

    default boolean shouldCancelEntityAttacking() {
        return true;
    }

    default boolean shouldCancelBlockBreaking() {
        return true;
    }
}
