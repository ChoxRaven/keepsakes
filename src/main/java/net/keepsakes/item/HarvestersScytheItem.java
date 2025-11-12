package net.keepsakes.item;

import de.dafuqs.additionalentityattributes.AdditionalEntityAttributes;
import net.keepsakes.Keepsakes;
import net.keepsakes.index.ModSounds;
import net.keepsakes.item.base.CustomCriticalHitItem;
import net.keepsakes.item.base.CustomPrimaryUseItem;
import net.keepsakes.item.base.CustomStatsTooltipItem;
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
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HarvestersScytheItem extends SwordItem implements CustomCriticalHitItem, CustomPrimaryUseItem, CustomStatsTooltipItem {
    private static final Identifier ENTITY_REACH_MODIFIER_ID = Identifier.of(Keepsakes.MOD_ID, "harvesters_scythe_entity_reach_modifier");
    private static final Identifier CRIT_DAMAGE_MODIFIER_ID = Identifier.of(Keepsakes.MOD_ID, "harvesters_scythe_entity_crit_damage_modifier");
    private static final float lifestealMultiplier = 0.15f;
    private static final float critDamageMultiplier = 1f;

    @Override
    public @Nullable CustomPayload getPrimaryUsePayload() {
        return null;
    }

    @Override
    public boolean shouldCancelEntityAttacking(PlayerEntity user) {
        float attackCooldownProgress = user.getAttackCooldownProgress(0f);

        return attackCooldownProgress != 1.0f;
    }

    @Override
    public boolean shouldCancelBlockBreaking(PlayerEntity user) {
        return false;
    }

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
                        critDamageMultiplier,
                        EntityAttributeModifier.Operation.ADD_VALUE
                ),
                AttributeModifierSlot.MAINHAND
        );

        return builder.build();
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        boolean showDetails = false;
        try {
            Class<?> screenClass = Class.forName("net.minecraft.client.gui.screen.Screen");
            var hasShiftDownMethod = screenClass.getMethod("hasShiftDown");
            showDetails = (Boolean) hasShiftDownMethod.invoke(null);
        } catch (ClassNotFoundException e) {
            Keepsakes.LOGGER.error("CLIENT: Screen class not found - this is expected on server");
        } catch (Exception e) {
            Keepsakes.LOGGER.error("CLIENT: Reflection failed: {}", e.getMessage(), e);
        }

        tooltip.add(Text.translatable("item.keepsakes.harvesters_scythe.lore").formatted(Formatting.DARK_GRAY));

        tooltip.add(Text.translatable("item.keepsakes.harvesters_scythe.ability").formatted(Formatting.GOLD)
                .append(Text.translatable(!showDetails ? "item.keepsakes.ability.hold_shift" : "").formatted(Formatting.DARK_GRAY)));

        if (showDetails) {
            tooltip.add(Text.translatable("item.keepsakes.harvesters_scythe.ability_tooltip").formatted(Formatting.GRAY));
        }
    }

    @Override
    public void appendStatsTooltip(ItemStack stack, PlayerEntity user, List<Text> tooltip, TooltipType type) {
        int index = tooltip.indexOf(Text.translatable("item.modifiers.mainhand").formatted(Formatting.DARK_GRAY));

        tooltip.add(index + 1, Text.translatable("item.keepsakes.keywords.unwieldy").formatted(Formatting.DARK_GREEN));
    }

    @Override
    public void postCrit(ItemStack stack, LivingEntity target,  PlayerEntity attacker) {
        float damageDealt = target.getMaxHealth() - target.getHealth();
        float healingAmount = damageDealt * lifestealMultiplier;

        attacker.getWorld().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), ModSounds.HARVESTERS_SCYTHE_CRITICAL, attacker.getSoundCategory(), 1.0f, (float) (1.0f + attacker.getRandom().nextGaussian() / 10f));

        attacker.heal(healingAmount);
    }
}