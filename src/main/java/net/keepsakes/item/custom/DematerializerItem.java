package net.keepsakes.item.custom;

import net.keepsakes.Keepsakes;
import net.keepsakes.item.CustomPrimaryUseItem;
import net.keepsakes.networking.packet.DematerializerLeftClickPayload;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class DematerializerItem extends Item implements CustomPrimaryUseItem {
    public DematerializerItem(Settings settings) {
        super(settings.maxCount(1).fireproof());
    }

    @Override
    public CustomPayload getPrimaryUsePayload() {
        return new DematerializerLeftClickPayload();
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        if (world.isClient) {
            sendRightClickPacket(user);
        }

        return TypedActionResult.success(itemStack, false);
    }

    private static void sendRightClickPacket(PlayerEntity user) {
        try {
            Class<?> clientNetworking = Class.forName("net.keepsakes.networking.ClientNetworking");
            var method = clientNetworking.getMethod("sendDematerializerRightClick", PlayerEntity.class);
            method.invoke(null, user);
        } catch (ClassNotFoundException e) {
            Keepsakes.LOGGER.error("CLIENT: ClientNetworking class not found - this is expected on server");
        } catch (Exception e) {
            Keepsakes.LOGGER.error("CLIENT: Reflection failed: {}", e.getMessage(), e);
        }
    }
}