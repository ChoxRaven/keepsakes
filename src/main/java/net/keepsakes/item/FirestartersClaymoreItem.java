package net.keepsakes.item;

import net.keepsakes.item.base.CustomCriticalHitItem;
import net.keepsakes.item.base.CustomPrimaryUseItem;
import net.keepsakes.item.base.CustomStatsTooltipItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FirestartersClaymoreItem extends SwordItem implements CustomCriticalHitItem, CustomPrimaryUseItem, CustomStatsTooltipItem {
    public FirestartersClaymoreItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public @Nullable CustomPayload getPrimaryUsePayload() {
        return null;
    }

    @Override
    public void appendStatsTooltip(ItemStack stack, PlayerEntity user, List<Text> tooltip, TooltipType type) {
        int index = tooltip.indexOf(Text.translatable("item.modifiers.mainhand").formatted(Formatting.DARK_GRAY));

        tooltip.add(index + 1, Text.translatable("item.keepsakes.keywords.relentless").formatted(Formatting.DARK_GREEN));
        tooltip.add(index + 1, Text.translatable("item.keepsakes.keywords.charged").formatted(Formatting.DARK_GREEN));
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (target.isOnFire()) {
            float fireDamage = target.getFireTicks() / 2f;

            target.damage(attacker.getDamageSources().onFire(), fireDamage);
            target.setOnFireForTicks(0);
        }
        return true;
    }
}
