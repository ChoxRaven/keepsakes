package net.keepsakes.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.keepsakes.Keepsakes;
import net.keepsakes.index.ModItems;
import net.keepsakes.item.logic.DematerializerLogic;
import net.keepsakes.networking.packet.DematerializerLeftClickPayload;
import net.keepsakes.networking.packet.DematerializerRightClickPayload;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class ModNetworking {
    public static void registerC2SPayloads() {
        DematerializerLeftClickPayload.register();
        DematerializerRightClickPayload.register();
    }

    public static void registerS2CPayloads() {
        // Register server-to-client payloads here when needed
    }

    public static void registerServerReceivers() {
        // Dematerializer left-click handler
        ServerPlayNetworking.registerGlobalReceiver(
                DematerializerLeftClickPayload.ID,
                (payload, context) -> {
                    context.server().execute(() -> {
                        var player = context.player();
                        var world = player.getWorld();
                        ItemStack mainHandStack = player.getMainHandStack();

                        if (!mainHandStack.isEmpty() && mainHandStack.getItem() == ModItems.DEMATERIALIZER) {
                            Keepsakes.LOGGER.info("SERVER: Calling handleLeftClick on Dematerializer");

                            DematerializerLogic.handleLeftClick(world, player, mainHandStack);
                        }
                    });
                }
        );

        // Dematerializer right-click handler
        ServerPlayNetworking.registerGlobalReceiver(
                DematerializerRightClickPayload.ID,
                (payload, context) -> {
                    ServerPlayerEntity player = context.player();
                    Keepsakes.LOGGER.info("SERVER: Received right-click packet for position {}", payload.targetPos());
                    Keepsakes.LOGGER.info("SERVER: Player position: {}", player.getBlockPos());

                    BlockState targetState = player.getWorld().getBlockState(payload.targetPos());
                    Keepsakes.LOGGER.info("SERVER: Block at target position: {}", targetState.getBlock());

                    DematerializerLogic.handleRightClick(player.getWorld(), player, payload.targetPos());
                });
    }

    // Initialize all networking components
    public static void initialize() {
        registerC2SPayloads();
        registerS2CPayloads();
        registerServerReceivers();
    }
}