package net.keepsakes.item.custom;

import com.google.common.collect.Multimap;
import com.mojang.datafixers.kinds.IdF;
import net.keepsakes.Keepsakes;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.UnbreakableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.UUID;

public class HarvestersScytheItem extends SwordItem {
    private static final Identifier ENTITY_REACH_ID = Identifier.of(Keepsakes.MOD_ID, "harvesters_scythe_entity_reach_modifier");

    public HarvestersScytheItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings
                .maxCount(1)
                .component(DataComponentTypes.UNBREAKABLE, new UnbreakableComponent(true))
                .component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createHarvestersScytheModifiers(toolMaterial))
                .fireproof()
        );
    }
    
    

    private static AttributeModifiersComponent createHarvestersScytheModifiers(ToolMaterial toolMaterial) {
        AttributeModifiersComponent.Builder builder = AttributeModifiersComponent.builder();

        builder.add(
                EntityAttributes.GENERIC_ATTACK_DAMAGE,
                new EntityAttributeModifier(
                        BASE_ATTACK_DAMAGE_MODIFIER_ID,
                        toolMaterial.getAttackDamage() + 2,
                        EntityAttributeModifier.Operation.ADD_VALUE
                ),
                AttributeModifierSlot.MAINHAND
        );

        builder.add(
                EntityAttributes.GENERIC_ATTACK_SPEED,
                new EntityAttributeModifier(
                        BASE_ATTACK_SPEED_MODIFIER_ID,
                        -3.2f,
                        EntityAttributeModifier.Operation.ADD_VALUE
                ),
                AttributeModifierSlot.MAINHAND
        );

        // Add reach modifier
        builder.add(
                EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE,
                new EntityAttributeModifier(
                        ENTITY_REACH_ID,
                        0.5,
                        EntityAttributeModifier.Operation.ADD_VALUE
                ),
                AttributeModifierSlot.MAINHAND
        );

        return builder.build();
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.keepsakes.harvesters_scythe.tooltip").formatted(Formatting.DARK_GRAY));
        tooltip.add(Text.translatable("item.keepsakes.harvesters_scythe.explanation1").formatted(Formatting.GREEN));
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity) {
            float damageDealt = target.getMaxHealth() - target.getHealth();
            float healingAmount = damageDealt * 0.15f;

            attacker.heal(healingAmount);
        }
        return true;
    }
}