package net.keepsakes.item.base;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

import java.util.List;

public interface CustomTooltipItem {
    void appendCustomTooltip(ItemStack stack, PlayerEntity user, List<Text> tooltip, TooltipType type);
}
