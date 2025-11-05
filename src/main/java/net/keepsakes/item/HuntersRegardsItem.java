package net.keepsakes.item;

import net.keepsakes.item.base.CustomPrimaryUseItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class HuntersRegardsItem extends Item implements CustomPrimaryUseItem {
    public HuntersRegardsItem(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable CustomPayload getPrimaryUsePayload() {
        return null;
    }
}
