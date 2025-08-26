package net.keepsakes.item.custom;

import net.keepsakes.Keepsakes;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;

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

    // * Per tick, while in the player hotbar
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        // * Skip if not in hotbar (slots 0-8)
        if (slot > 8) {
            return;
        }

        // * Make sure the entity is a player
        if (entity instanceof PlayerEntity player) {
            // * spawn client-side particles
            if (world.isClient) {
                Random random = world.getRandom();
                // * Spawn particles around the player
//              if (random.nextFloat() < 0.4f) { // ? 40% chance each tick to spawn particles
//                  double x = player.getX() + (random.nextFloat() * 2.0 - 1.0) * 0.5;
//                  double y = player.getY() + random.nextFloat() * 2.0;
//                  double z = player.getZ() + (random.nextFloat() * 2.0 - 1.0) * 0.5;
//
//                  double vx = (random.nextFloat() * 2.0 - 1.0) * 0.01;
//                  double vy = random.nextFloat() * 0.05;
//                  double vz = (random.nextFloat() * 2.0 - 1.0) * 0.01;
//
//                  world.addParticle(ParticleTypes.SOUL, x, y, z, vx, vy, vz);
//              }
                // * Spawn particles on the player
                // TODO: make the particle follow the player smoothly
                Vec3d playerVelocity = player.getVelocity();
                world.addParticle(Keepsakes.AMBITION_HALO_PARTICLE, player.getX(), player.getY() + 2.0f, player.getZ(), playerVelocity.x, playerVelocity.y, playerVelocity.z);
            }
        }
    }
}
