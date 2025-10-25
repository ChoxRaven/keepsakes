package net.keepsakes.item.custom;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.UnbreakableComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class HarvestersScytheItem extends SwordItem {
    public HarvestersScytheItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings
                .maxCount(1)
                .component(DataComponentTypes.UNBREAKABLE, new UnbreakableComponent(true))
                .attributeModifiers(SwordItem.createAttributeModifiers(toolMaterial, 3, -3.0f))
                .fireproof()
        );
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.keepsakes.harvesters_scythe.tooltip").formatted(Formatting.DARK_GRAY));
        tooltip.add(Text.translatable("item.keepsakes.harvesters_scythe.explanation1").formatted(Formatting.GREEN));
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity) {
            // * How much the Player should heal
            float damageDealt = target.getMaxHealth() - target.getHealth();
            float healingAmount = damageDealt * 0.1f;

            attacker.heal(healingAmount);
        }
        return true;
    }
}