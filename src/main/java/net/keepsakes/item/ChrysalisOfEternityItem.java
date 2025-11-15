package net.keepsakes.item;

import io.wispforest.accessories.api.attributes.AccessoryAttributeBuilder;
import io.wispforest.accessories.api.slot.SlotReference;
import net.keepsakes.Keepsakes;
import net.keepsakes.item.base.GenericAccessoryItem;
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

import java.text.Normalizer;
import java.util.List;

public class ChrysalisOfEternityItem extends GenericAccessoryItem {
    // ? Attribute Modifier IDs
    private static final Identifier DEFENSE_ID = Identifier.of(Keepsakes.MOD_ID, "ambition_defense_modifier");
    private static final Identifier ATTACK_DAMAGE_ID = Identifier.of(Keepsakes.MOD_ID, "ambition_damage_modifier");
    private static final Identifier ATTACK_SPEED_ID = Identifier.of(Keepsakes.MOD_ID, "ambition_attack_speed_modifier");

    // * Item Settings
    public ChrysalisOfEternityItem(Settings settings) {
        super(settings.fireproof(), 2, false);
    }

    @Override
    protected boolean canCycleState(ItemStack stack, PlayerEntity player) {
        return !isAbilityLocked(stack) && player.getHealth() >= player.getMaxHealth() / 2f;
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        NbtCompound nbt = new NbtCompound();

        nbt.putInt("AbilityState", 0);
        nbt.putBoolean("AbilityLocked", false);

        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
        return stack;
    }

    @Override
    protected void onStateChanged(ItemStack stack, PlayerEntity player, int oldState, int newState) {
        World world = player.getWorld();
        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                newState == 1 ? SoundEvents.BLOCK_SOUL_SAND_BREAK : SoundEvents.BLOCK_SOUL_SAND_PLACE,
                player.getSoundCategory(), 2f, 1.0f);

        Formatting formatting = newState == 1 ? Formatting.LIGHT_PURPLE : Formatting.GRAY;

        if (world.isClient) {
            player.sendMessage(Text.translatable("item.keepsakes.ability.status").formatted(Formatting.GRAY)
                    .append(Text.literal(newState == 1 ? " ON" : " OFF").formatted(formatting)), true);
        }
    }

    @Override
    protected void onStateChangeBlocked(ItemStack stack, PlayerEntity player, int currentState) {
        player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BLOCK_CHAIN_PLACE, player.getSoundCategory(), 0.5f, 0.8f);

        // Send locked message
        if (player.getHealth() <= player.getMaxHealth() / 2f) {
            player.sendMessage(Text.translatable("item.keepsakes.ability.cycle_failed").formatted(Formatting.GRAY)
                    .append(Text.translatable("item.keepsakes.chrysalis_of_eternity.ability_toggle_requirement").formatted(Formatting.RED)), true);
        } else {
            player.sendMessage(Text.translatable("item.keepsakes.ability.cycle_failed").formatted(Formatting.GRAY)
                    .append(Text.translatable("item.keepsakes.ability.locked")), true);
        }
    }

    // * Tooltip
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

        // ? Lore
        tooltip.add(Text.translatable("item.keepsakes.chrysalis_of_eternity.lore").formatted(Formatting.DARK_GRAY));

        // ? Toggle info
        Formatting formatting = getAbilityState(stack) == 1 ? Formatting.LIGHT_PURPLE : Formatting.GRAY;
        tooltip.add(Text.translatable("item.keepsakes.misc.toggle_info").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("item.keepsakes.ability.cycle_info").formatted(Formatting.GRAY)
                .append(Text.translatable("item.keepsakes.chrysalis_of_eternity.ability_toggle_requirement").formatted(Formatting.RED)));
        tooltip.add(Text.translatable("item.keepsakes.ability.status").formatted(Formatting.GRAY)
                .append(Text.literal(getAbilityState(stack) == 1 ? " On" : " Off").formatted(formatting)));

        tooltip.add(Text.translatable("item.keepsakes.chrysalis_of_eternity.ability").formatted(Formatting.GOLD)
                .append(Text.translatable(!showDetails ? "item.keepsakes.ability.hold_shift" : "").formatted(Formatting.DARK_GRAY)));
        if (showDetails) {
            tooltip.add(Text.translatable("item.keepsakes.chrysalis_of_eternity.ability_tooltip").formatted(Formatting.GRAY));
        }
    }

    @Override
    public void getModifiers(ItemStack stack, SlotReference reference, AccessoryAttributeBuilder builder) {
        if (reference.slotName().equals("hat")) {
            if (getAbilityState(stack) == 1) {
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
        if (!(player.getEntityWorld().isClient) && getAbilityState(stack) == 1) {
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
