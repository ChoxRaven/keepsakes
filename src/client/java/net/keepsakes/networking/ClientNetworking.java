package net.keepsakes.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.keepsakes.Keepsakes;
import net.keepsakes.networking.packet.DematerializerRightClickPayload;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

public class ClientNetworking {
    public static void sendDematerializerRightClick(PlayerEntity user) {
        HitResult hit = user.raycast(10.0, 0.0f, false);

        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hit;

            try {
                ClientPlayNetworking.send(new DematerializerRightClickPayload(blockHit.getBlockPos()));
            } catch (Exception e) {
                Keepsakes.LOGGER.error("CLIENT: Failed to send packet: {}", e.getMessage(), e);
            }
        }
    }
}
