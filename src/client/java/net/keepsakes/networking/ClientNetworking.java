package net.keepsakes.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.keepsakes.Keepsakes;
import net.keepsakes.networking.packet.DematerializerRightClickPayload;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

public class ClientNetworking {
    public static void sendDematerializerRightClick(PlayerEntity user) {
        Keepsakes.LOGGER.info("CLIENT: Starting right-click packet send");

        // Perform raycast to find what block we're looking at
        HitResult hit = user.raycast(10.0, 0.0f, false);
        Keepsakes.LOGGER.info("CLIENT: Raycast result type: {}", hit.getType());

        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hit;
            Keepsakes.LOGGER.info("CLIENT: Targeting block at {}", blockHit.getBlockPos());

            try {
                // Send the packet
                ClientPlayNetworking.send(new DematerializerRightClickPayload(blockHit.getBlockPos()));
                Keepsakes.LOGGER.info("CLIENT: Packet sent successfully for block at {}", blockHit.getBlockPos());
            } catch (Exception e) {
                Keepsakes.LOGGER.error("CLIENT: Failed to send packet: {}", e.getMessage(), e);
            }
        } else {
            Keepsakes.LOGGER.info("CLIENT: No block targeted - hit type: {}", hit.getType());
        }
    }
}
