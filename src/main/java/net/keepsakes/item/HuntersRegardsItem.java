package net.keepsakes.item;

import net.keepsakes.Keepsakes;
import net.keepsakes.item.base.CustomPrimaryUseItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.UnbreakableComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class HuntersRegardsItem extends SwordItem implements CustomPrimaryUseItem {
    private static final Identifier ENTITY_REACH_MODIFIER_ID = Identifier.of(Keepsakes.MOD_ID, "hunters_regards_entity_reach_modifier");

    public HuntersRegardsItem(ToolMaterial toolMaterial, Settings settings) {
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
                        -3.8f,
                        EntityAttributeModifier.Operation.ADD_VALUE
                ),
                AttributeModifierSlot.MAINHAND
        );

        builder.add(
                EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE,
                new EntityAttributeModifier(
                        ENTITY_REACH_MODIFIER_ID,
                        -1,
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
}