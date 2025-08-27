package net.keepsakes.item.custom;

import net.keepsakes.Keepsakes;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ChrysalisOfEternity extends Item {
    public ChrysalisOfEternity(Settings settings) {
        super(settings
                .maxCount(1)
                .fireproof()
        );
    }

    // * Tooltip
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        // ? Hotbar requirement info
        tooltip.add(Text.translatable("item.keepsakes.misc.passive_info").formatted(Formatting.LIGHT_PURPLE));

        // ? Lore
        tooltip.add(Text.translatable("item.keepsakes.chrysalis_of_eternity.tooltip").formatted(Formatting.GRAY));

        // * Add King Azamoth's Ambition toggle status to tooltip
        boolean ambitionEnabled = isAmbitionEnabled(stack);
        Formatting frostwalkerFormatting = ambitionEnabled ? Formatting.LIGHT_PURPLE : Formatting.GRAY;
        tooltip.add(Text.translatable("item.keepsakes.chrysalis_of_eternity.king_azamoths_ambition",
                ambitionEnabled ? "ON" : "OFF").formatted(frostwalkerFormatting));

        // ? Explanation for King Azamoth's Ambition
        tooltip.add(Text.translatable("item.keepsakes.chrysalis_of_eternity.stats_explanation1").formatted(frostwalkerFormatting));
        tooltip.add(Text.translatable("item.keepsakes.chrysalis_of_eternity.stats_explanation2").formatted(frostwalkerFormatting));
        tooltip.add(Text.translatable("item.keepsakes.chrysalis_of_eternity.stats_explanation3").formatted(frostwalkerFormatting));
    }

    // ? Toggle for King Azamoth's Ambition
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        // * Toggle King Azamoth's Ambition
        boolean currentState = isAmbitionEnabled(stack);
        setAmbitionStatus(stack, !currentState);

        // * Play sound effect for feedback
        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                currentState ? SoundEvents.BLOCK_SNOW_BREAK : SoundEvents.BLOCK_GLASS_PLACE,
                SoundCategory.PLAYERS, 2f, currentState ? 0.5f : 1.2f);

        // * Send message to player
        if (world.isClient) {
            boolean newState = !currentState;
            Formatting formatting = newState ? Formatting.LIGHT_PURPLE : Formatting.GRAY;
            user.sendMessage(Text.translatable("item.keepsakes.chrysalis_of_eternity.king_azamoths_ambition",
                    newState ? "ON" : "OFF").formatted(formatting), true);
        }

        return TypedActionResult.success(stack, true);
    }

    // ? Helper methods for item components
    private boolean isAmbitionEnabled(ItemStack stack) {
        // * Check if the stack has the custom_data component with the Ambition property
        NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (customData != null) {
            // * Get the NBT compound and check for our property
            return customData.copyNbt().getBoolean("Ambition");
        }
        return false;
    }

    private void setAmbitionStatus(ItemStack stack, boolean enabled) {
        // * Get or create the custom_data component
        NbtComponent customData = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        // * Create a new NBT compound with the property
        NbtCompound nbt = customData.copyNbt();
        nbt.putBoolean("Ambition", enabled);
        // * Set the updated component
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    // ? IDs
    private static final Identifier DEFENSE_ID = Identifier.of(Keepsakes.MOD_ID, "ambition_defense_modifier");
    private static final Identifier DAMAGE_ID = Identifier.of(Keepsakes.MOD_ID, "ambition_damage_modifier");
    private static final Identifier ATTACK_SPEED_ID = Identifier.of(Keepsakes.MOD_ID, "ambition_attack_speed_modifier");

    // ? Attribute Modifiers
    private static final EntityAttributeModifier AMBITION_DEFENSE_MODIFIER = new EntityAttributeModifier(
            DEFENSE_ID,
            -6f,
            EntityAttributeModifier.Operation.ADD_VALUE
    );

    private static final EntityAttributeModifier AMBITION_DAMAGE_MODIFIER = new EntityAttributeModifier(
            DAMAGE_ID,
            0.5f,
            EntityAttributeModifier.Operation.ADD_VALUE
    );

    private static final EntityAttributeModifier AMBITION_ATTACK_SPEED_MODIFIER = new EntityAttributeModifier(
            ATTACK_SPEED_ID,
            0.2f,
            EntityAttributeModifier.Operation.ADD_VALUE
    );

    private void applyOrRemoveModifiers(PlayerEntity player, boolean inHotbar, boolean ambitionStatus) {
        if (inHotbar && ambitionStatus) {
            // Check if modifiers are not already present before adding
            if (!Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR)).hasModifier(AMBITION_DEFENSE_MODIFIER.id())) {
                Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR)).addTemporaryModifier(AMBITION_DEFENSE_MODIFIER);
            }
            if (!Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)).hasModifier(AMBITION_DAMAGE_MODIFIER.id())) {
                Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)).addTemporaryModifier(AMBITION_DAMAGE_MODIFIER);
            }
            if (!Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_SPEED)).hasModifier(AMBITION_ATTACK_SPEED_MODIFIER.id())) {
                Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_SPEED)).addTemporaryModifier(AMBITION_ATTACK_SPEED_MODIFIER);
            }
        } else {
            // Remove modifiers if the item is no longer in the hotbar / active
            Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR)).removeModifier(AMBITION_DEFENSE_MODIFIER.id());
            Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)).removeModifier(AMBITION_DAMAGE_MODIFIER.id());
            Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_SPEED)).removeModifier(AMBITION_ATTACK_SPEED_MODIFIER.id());
        }
    }

    // * Per tick, while in the player hotbar
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        // * Check if the item is in the player's hotbar
        boolean inHotbar = (slot < 9);

        // * Make sure the entity is a player
        if (entity instanceof PlayerEntity player) {
            // ? Run only serverside to ensure data is synced
            if (!world.isClient) {
                Random random = world.getRandom();
                applyOrRemoveModifiers(player, inHotbar, isAmbitionEnabled(stack));
            }
        }
    }
}
