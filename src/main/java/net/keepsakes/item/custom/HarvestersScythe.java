package net.keepsakes.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class HarvestersScythe extends SwordItem {
    public HarvestersScythe(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings.attributeModifiers(SwordItem.createAttributeModifiers(toolMaterial, 8, -3.2f)));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.keepsakes.harvesters_scythe.tooltip").formatted(Formatting.GRAY));
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity) {
            float damageDealt = target.getMaxHealth() - target.getHealth();
            float healingAmount = damageDealt * 0.1f;
            attacker.heal(healingAmount);
        }
        return super.postHit(stack, target, attacker);
    }
}