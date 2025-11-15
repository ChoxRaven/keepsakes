package net.keepsakes.item;

import net.keepsakes.Keepsakes;
import net.keepsakes.item.base.CustomPrimaryUseItem;
import net.keepsakes.item.base.CustomTooltipItem;
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
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FirestartersClaymoreItem extends SwordItem implements CustomPrimaryUseItem, CustomTooltipItem {
    public FirestartersClaymoreItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings
                .maxCount(1)
                .component(DataComponentTypes.UNBREAKABLE, new UnbreakableComponent(true))
                .component(DataComponentTypes.ATTRIBUTE_MODIFIERS, createFirestartersClaymoreModifiers(toolMaterial))
                .fireproof()
        );
    }

    @Override
    public boolean shouldCancelEntityAttacking(PlayerEntity user) {
        return false;
    }

    private static AttributeModifiersComponent createFirestartersClaymoreModifiers(ToolMaterial toolMaterial) {
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

        return builder.build();
    }

    @Override
    public @Nullable CustomPayload getPrimaryUsePayload() {
        return null;
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

        tooltip.add(Text.translatable("item.keepsakes.firestarters_claymore.lore").formatted(Formatting.DARK_GRAY));

        tooltip.add(Text.translatable("item.keepsakes.firestarters_claymore.ability").formatted(Formatting.GOLD)
                .append(Text.translatable(!showDetails ? "item.keepsakes.ability.hold_shift" : "").formatted(Formatting.DARK_GRAY)));

        if (showDetails) {
            tooltip.add(Text.translatable("item.keepsakes.firestarters_claymore.ability_tooltip").formatted(Formatting.GRAY));
        }
    }

    @Override
    public void appendCustomTooltip(ItemStack stack, PlayerEntity user, List<Text> tooltip, TooltipType type) {
        int index = tooltip.indexOf(Text.translatable("item.modifiers.mainhand").formatted(Formatting.GRAY));

        tooltip.add(index + 1, Text.translatable("item.keepsakes.keywords.charged").formatted(Formatting.DARK_GREEN));
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (target.isOnFire()) {
            float fireDamage = target.getFireTicks() / 20f;

            target.damage(attacker.getDamageSources().onFire(), fireDamage);
            attacker.getWorld().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.BLOCK_LAVA_EXTINGUISH, attacker.getSoundCategory(), 1.0f, (float) (1.0f + attacker.getRandom().nextGaussian() / 10f));
            target.setFireTicks(0);
        }
        return true;
    }
}
