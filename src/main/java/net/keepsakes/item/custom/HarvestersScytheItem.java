package net.keepsakes.item.custom;

import de.dafuqs.additionalentityattributes.AdditionalEntityAttributes;
import net.keepsakes.Keepsakes;
import net.keepsakes.index.ModSounds;
import net.keepsakes.item.CustomCriticalHitItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.UnbreakableComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

import java.util.List;

public class HarvestersScytheItem extends SwordItem implements CustomCriticalHitItem {
    private static final Identifier ENTITY_REACH_MODIFIER_ID = Identifier.of(Keepsakes.MOD_ID, "harvesters_scythe_entity_reach_modifier");
    private static final Identifier CRIT_DAMAGE_MODIFIER_ID = Identifier.of(Keepsakes.MOD_ID, "harvesters_scythe_entity_crit_damage_modifier");

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

        builder.add(
                EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE,
                new EntityAttributeModifier(
                        ENTITY_REACH_MODIFIER_ID,
                        0.5,
                        EntityAttributeModifier.Operation.ADD_VALUE
                ),
                AttributeModifierSlot.MAINHAND
        );

        builder.add(
                AdditionalEntityAttributes.CRITICAL_BONUS_DAMAGE,
                new EntityAttributeModifier(
                        CRIT_DAMAGE_MODIFIER_ID,
                        1,
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
    public void postCrit(ItemStack stack, LivingEntity target,  PlayerEntity attacker) {
        float damageDealt = target.getMaxHealth() - target.getHealth();
        float healingAmount = damageDealt * 0.15f;

        attacker.getWorld().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), ModSounds.HARVESTERS_SCYTHE_CRITICAL, attacker.getSoundCategory(), 1.0f, 1.0f);

        attacker.heal(healingAmount);
    }
}