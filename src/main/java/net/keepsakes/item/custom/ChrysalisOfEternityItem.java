package net.keepsakes.item.custom;

import io.wispforest.accessories.api.AccessoryItem;
import io.wispforest.accessories.api.attributes.AccessoryAttributeBuilder;
import io.wispforest.accessories.api.slot.SlotReference;
import net.keepsakes.Keepsakes;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;

public class ChrysalisOfEternityItem extends AccessoryItem {
    // * Item Settings
    public ChrysalisOfEternityItem(Settings settings) {
        super(settings
                .maxCount(1)
                .fireproof()
        );
    }

    // * Tooltip
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        // ? Toggle info
        tooltip.add(Text.translatable("item.keepsakes.misc.toggle_info").formatted(Formatting.LIGHT_PURPLE));

        // ? Lore
        tooltip.add(Text.translatable("item.keepsakes.chrysalis_of_eternity.tooltip").formatted(Formatting.DARK_GRAY));

        // * Add King Azamoth's Ambition toggle status to tooltip
        boolean ambitionEnabled = isAmbitionEnabled(stack);
        Formatting ambitionFormatting = ambitionEnabled ? Formatting.LIGHT_PURPLE : Formatting.GRAY;
        tooltip.add(Text.translatable("item.keepsakes.chrysalis_of_eternity.king_azamoths_ambition",
                ambitionEnabled ? "<neon>ON</neon>" : "OFF").formatted(ambitionFormatting));

        // ? Explanation for King Azamoth's Ambition
        tooltip.add(Text.translatable("item.keepsakes.chrysalis_of_eternity.stats_explanation1").formatted(ambitionFormatting));
        tooltip.add(Text.translatable("item.keepsakes.chrysalis_of_eternity.stats_explanation2").formatted(ambitionFormatting));
        tooltip.add(Text.translatable("item.keepsakes.chrysalis_of_eternity.stats_explanation3").formatted(ambitionFormatting));
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
                currentState ? SoundEvents.BLOCK_SOUL_SAND_BREAK : SoundEvents.BLOCK_SOUL_SAND_PLACE,
                SoundCategory.PLAYERS, 2f, currentState ? 0.5f : 1.2f);

        // * Send message to player
        if (world.isClient) {
            boolean newState = !currentState;
            Formatting formatting = newState ? Formatting.LIGHT_PURPLE : Formatting.GRAY;
            user.sendMessage(Text.translatable("item.keepsakes.chrysalis_of_eternity.king_azamoths_ambition",
                    newState ? "<neon>ON</neon>" : "OFF").formatted(formatting), true);
        }

        return TypedActionResult.success(stack, true);
    }

    // ? Helper methods for item components
    private boolean isAmbitionEnabled(ItemStack stack) {
        // * Check if the stack has the custom_data component with the Ambition property
        NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (customData != null) {
            // * Get the NBT compound and check for the property
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

    // ? Attribute Modifiers
    private static final Identifier DEFENSE_ID = Identifier.of(Keepsakes.MOD_ID, "ambition_defense_modifier");
    private static final Identifier ATTACK_DAMAGE_ID = Identifier.of(Keepsakes.MOD_ID, "ambition_damage_modifier");
    private static final Identifier ATTACK_SPEED_ID = Identifier.of(Keepsakes.MOD_ID, "ambition_attack_speed_modifier");

    @Override
    public void getModifiers(ItemStack stack, SlotReference reference, AccessoryAttributeBuilder builder) {
        if (reference.slotName().equals("hat")) {
            if (isAmbitionEnabled(stack)) {
                builder.addExclusive(EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(DEFENSE_ID, -6.0f, EntityAttributeModifier.Operation.ADD_VALUE));
                builder.addExclusive(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_ID, 0.5f, EntityAttributeModifier.Operation.ADD_VALUE));
                builder.addExclusive(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_ID, 0.2f, EntityAttributeModifier.Operation.ADD_VALUE));
            }
        }
    }

    // * Runs per tick while equipped by a player
    @Override
    public void tick(ItemStack stack, SlotReference reference) {
        if (!(reference.entity() instanceof PlayerEntity player)) {
            return;
        }

        if (player.isSpectator()) {
            return;
        }

        // * Spawn particles
        if (!(player.getEntityWorld().isClient) && isAmbitionEnabled(stack)) {
            Random random = player.getEntityWorld().getRandom();
            if (random.nextFloat() < 0.4f) { // ? 40% chance each tick to spawn particles
                double x = player.getX() + (random.nextFloat() * 2.0 - 1.0) * 0.5;
                double y = player.getY() + random.nextFloat() * 2.0;
                double z = player.getZ() + (random.nextFloat() * 2.0 - 1.0) * 0.5;

                double vx = (random.nextFloat() * 2.0 - 1.0) * 0.01;
                double vy = random.nextFloat() * 0.05;
                double vz = (random.nextFloat() * 2.0 - 1.0) * 0.01;

                ((ServerWorld) player.getWorld()).spawnParticles(
                        ParticleTypes.SOUL, x, y, z, 1, vx, vy, vz, 0.0
                );
            }
        }
    }
}
