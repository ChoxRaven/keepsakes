package net.keepsakes.item;
import io.wispforest.accessories.api.slot.SlotReference;
import net.keepsakes.Keepsakes;
import net.keepsakes.item.base.GenericAccessoryItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import java.util.List;

public class EternalSnowflakeItem extends GenericAccessoryItem {
    // * Item Settings
    public EternalSnowflakeItem(Settings settings) {
        super(settings.fireproof(), 2, false);
    }

    // ? Freezes all water under the player, in a radius
    private void freezeWater(World world, PlayerEntity player) {
        if (world.isClient) {
            return; // Ensure this only runs on server
        }

        BlockPos playerPos = player.getBlockPos();
        int radius = 3;
        int yOffset = -1;
        
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                BlockPos pos = playerPos.add(x, yOffset, z);
                if (shouldFreezeWater(world, pos, player)) {
                    freezeSingleBlock(world, pos, player);
                }
            }
        }
    }
    
    private boolean shouldFreezeWater(World world, BlockPos pos, PlayerEntity player) {
        // * Checks if the block is within a specified distance from the Player
        if (!pos.isWithinDistance(new Vec3d(player.getX(), player.getY(), player.getZ()), 3.5f)) {
            return false;
        }

        // * Checks if the water block is a source block
        BlockState waterState = world.getBlockState(pos);
        if (waterState.getBlock() != Blocks.WATER || !waterState.getFluidState().isStill()) {
            return false;
        }

        // * Checks if the water block is under air, and above a solid, or another water block
        BlockPos belowPos = pos.down();
        BlockState belowState = world.getBlockState(belowPos);
        return (belowState.isSolidBlock(world, belowPos) || belowState.getBlock() == Blocks.WATER) && world.isAir(pos.up());
    }
    
    private void freezeSingleBlock(World world, BlockPos pos, PlayerEntity player) {
        if (world.isClient) {
            return; // Ensure this only runs on server
        }
        
        BlockState iceState = Blocks.FROSTED_ICE.getDefaultState();
        world.setBlockState(pos, iceState, 11); // Flag 11 = UPDATE_NEIGHBORS + UPDATE_CLIENTS
        
        // * Schedule a tick for the frosted ice to melt (vanilla Frost Walker uses 600 ticks)
        world.scheduleBlockTick(pos, Blocks.FROSTED_ICE, 
                         MathHelper.nextInt(player.getRandom(), 600, 800));
    }

    @Override
    protected void onStateChanged(ItemStack stack, PlayerEntity player, int oldState, int newState) {
        World world = player.getWorld();

        Formatting formatting = newState == 1 ? Formatting.AQUA : Formatting.GRAY;

        if (world.isClient) {
            player.sendMessage(Text.translatable("item.keepsakes.ability.status").formatted(Formatting.GRAY)
                    .append(Text.literal(newState == 1 ? " On" : " Off").formatted(formatting)), true);

            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    newState == 1 ? SoundEvents.BLOCK_SNOW_BREAK : SoundEvents.BLOCK_GLASS_PLACE,
                    player.getSoundCategory(), 2f, 1.0f);
        }
    }

    @Override
    protected void onStateChangeBlocked(ItemStack stack, PlayerEntity player, int currentState) {
        World world = player.getWorld();

        if (world.isClient) {
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BLOCK_CHAIN_PLACE, player.getSoundCategory(), 0.5f, 0.8f);

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
        tooltip.add(Text.translatable("item.keepsakes.eternal_snowflake.lore").formatted(Formatting.DARK_GRAY));

        // ? Toggle info
        Formatting formatting = getAbilityState(stack) == 1 ? Formatting.AQUA : Formatting.GRAY;
        tooltip.add(Text.translatable("item.keepsakes.misc.toggle_info").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("item.keepsakes.ability.status").formatted(Formatting.GRAY)
                .append(Text.literal(getAbilityState(stack) == 1 ? " On" : " Off").formatted(formatting)));

        tooltip.add(Text.translatable("item.keepsakes.eternal_snowflake.ability").formatted(Formatting.GOLD)
                .append(Text.translatable(!showDetails ? "item.keepsakes.ability.hold_shift" : "").formatted(Formatting.DARK_GRAY)));
        if (showDetails) {
            tooltip.add(Text.translatable("item.keepsakes.eternal_snowflake.ability_tooltip").formatted(Formatting.GRAY));
        }
    }

    // * Runs per tick while equipped by a player
    @Override
    public void tick(ItemStack stack, SlotReference reference) {
        if (!(reference.entity() instanceof PlayerEntity player) || player.isSpectator()) {
            return;
        }

        // * Spawn particles, and freeze
        if (!(player.getEntityWorld().isClient) && getAbilityState(stack) == 1) {
            // * Freeze all water sources around the player
            freezeWater(player.getEntityWorld(), player);

            Random random = player.getEntityWorld().getRandom();
            if (random.nextFloat() < 0.4f) { // ? 40% chance each tick to spawn particles
                double x = player.getX() + (random.nextFloat() * 2.0 - 1.0) * 1;
                double y = player.getY() + random.nextFloat() * 0.2;
                double z = player.getZ() + (random.nextFloat() * 2.0 - 1.0) * 1;

                double vx = (random.nextFloat() - 0.5) * 0.3;
                double vy = random.nextFloat() * 0.02;
                double vz = (random.nextFloat() - 0.5) * 0.3;

                ((ServerWorld) player.getWorld()).spawnParticles(
                        ParticleTypes.SNOWFLAKE, x, y, z, 1, vx, vy, vz, 0.0
                );
            }
        }
    }
}