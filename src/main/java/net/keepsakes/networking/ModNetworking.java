package net.keepsakes.networking;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.keepsakes.Keepsakes;
import net.keepsakes.item.ModItems;
import net.keepsakes.item.logic.DematerializerLogic;
import net.keepsakes.networking.packet.DematerializerLeftClickPayload;
import net.minecraft.item.ItemStack;

public class ModNetworking {

    /**
     * Registers all custom payloads for client-to-server communication
     */
    public static void registerC2SPayloads() {
        PayloadTypeRegistry.playC2S().register(
                DematerializerLeftClickPayload.ID,
                DematerializerLeftClickPayload.CODEC
        );
    }

    /**
     * Registers all custom payloads for server-to-client communication
     */
    public static void registerS2CPayloads() {
        // Register server-to-client payloads here when needed
    }

    /**
     * Registers all packet receivers on the server side
     */
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

        // Register more receivers here as needed
    }

    // Initialize all networking components
    public static void initialize() {
        registerC2SPayloads();
        registerS2CPayloads();
        registerServerReceivers();
    }
}