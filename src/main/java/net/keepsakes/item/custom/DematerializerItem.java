package net.keepsakes.item.custom;

import net.keepsakes.Keepsakes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class DematerializerItem extends Item {
    public DematerializerItem(Settings settings) {
        super(settings.maxCount(1).fireproof());
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        Keepsakes.LOGGER.info("CLIENT: Dematerializer use() method called - hand: {}, world.isClient: {}", hand, world.isClient);

        if (world.isClient) {
            Keepsakes.LOGGER.info("CLIENT: Attempting to send right-click packet");
            sendRightClickPacket(user);
        }

        return TypedActionResult.success(itemStack, false);
    }

    private static void sendRightClickPacket(PlayerEntity user) {
        try {
            Keepsakes.LOGGER.info("CLIENT: Using reflection to call ClientNetworking");
            Class<?> clientNetworking = Class.forName("net.keepsakes.networking.ClientNetworking");
            var method = clientNetworking.getMethod("sendDematerializerRightClick", PlayerEntity.class);
            method.invoke(null, user);
            Keepsakes.LOGGER.info("CLIENT: Reflection call completed successfully");
        } catch (ClassNotFoundException e) {
            Keepsakes.LOGGER.error("CLIENT: ClientNetworking class not found - this is expected on server");
        } catch (Exception e) {
            Keepsakes.LOGGER.error("CLIENT: Reflection failed: {}", e.getMessage(), e);
        }
    }
}