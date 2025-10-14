package net.keepsakes.item.custom;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import java.util.List;

public class EternalSnowflake extends Item {
    // * Item Settings
    public EternalSnowflake(Settings settings) {
        super(settings
                .maxCount(1)
                .fireproof()
        );
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

    // * Tooltip
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        // ? Hotbar requirement info
        tooltip.add(Text.translatable("item.keepsakes.misc.passive_info").formatted(Formatting.LIGHT_PURPLE));

        // ? Lore
        tooltip.add(Text.translatable("item.keepsakes.eternal_snowflake.tooltip").formatted(Formatting.BLUE));

        // * Add Enhanced Frost Walker toggle status to tooltip
        boolean frostWalkerEnabled = isFrostWalkerEnabled(stack);
        Formatting frostwalkerFormatting = frostWalkerEnabled ? Formatting.AQUA : Formatting.GRAY;
        tooltip.add(Text.translatable("item.keepsakes.eternal_snowflake.frost_walker",
                frostWalkerEnabled ? "ON" : "OFF").formatted(frostwalkerFormatting));
        
        // ? Explanation for Enhanced Frost Walker
        tooltip.add(Text.translatable("item.keepsakes.eternal_snowflake.frost_walker_explanation1").formatted(frostwalkerFormatting));
        tooltip.add(Text.translatable("item.keepsakes.eternal_snowflake.frost_walker_explanation2").formatted(frostwalkerFormatting));
        tooltip.add(Text.translatable("item.keepsakes.eternal_snowflake.frost_walker_explanation3").formatted(frostwalkerFormatting));
    }

    // ? Toggle for Frost Walker
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        // * Toggle Frostwalker
        boolean currentState = isFrostWalkerEnabled(stack);
        setFrostWalkerStatus(stack, !currentState);

        // * Play sound effect for feedback
        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                currentState ? SoundEvents.BLOCK_SNOW_BREAK : SoundEvents.BLOCK_GLASS_PLACE,
                SoundCategory.PLAYERS, 2f, currentState ? 0.5f : 1.2f);

        // * Send message to player
        if (world.isClient) {
            boolean newState = !currentState;
            Formatting formatting = newState ? Formatting.AQUA : Formatting.GRAY;
            user.sendMessage(Text.translatable("item.keepsakes.eternal_snowflake.frost_walker",
                    newState ? "ON" : "OFF").formatted(formatting), true);
        }

        return TypedActionResult.success(stack, true);
    }
    
    // ? Checks if Frost Walker is enabled
    private boolean isFrostWalkerEnabled(ItemStack stack) {
        // * Check if the stack has the custom_data component with our FrostWalker property
        NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (customData != null) {
            // * Get the NBT compound and check for Enhanced Frost Walker
            return customData.copyNbt().getBoolean("FrostWalker");
        }
        return false;
    }

    // ? Toggles Frost Walker
    private void setFrostWalkerStatus(ItemStack stack, boolean enabled) {
        // * Get or create the custom_data component
        NbtComponent customData = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        // * Create a new NBT compound with the property
        NbtCompound nbt = customData.copyNbt();
        nbt.putBoolean("FrostWalker", enabled);
        // * Set the updated component
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        // ? Hotbar check
        if (slot > 8) {
            return;
        }

        // ? Player check
        if (entity instanceof PlayerEntity player) {
            // * Spawn client-side particles
            if (world.isClient) {
                Random random = world.getRandom();
                if (random.nextFloat() < 0.4f) { // ? 40% chance each tick to spawn particles
                    double x = player.getX() + (random.nextFloat() * 2.0 - 1.0) * 0.5;
                    double y = player.getY() + random.nextFloat() * 2.0;
                    double z = player.getZ() + (random.nextFloat() * 2.0 - 1.0) * 0.5;

                    double vx = (random.nextFloat() * 2.0 - 1.0) * 0.01;
                    double vy = random.nextFloat() * 0.05;
                    double vz = (random.nextFloat() * 2.0 - 1.0) * 0.01;

                    world.addParticle(ParticleTypes.SNOWFLAKE, x, y, z, vx, vy, vz);
                }
            }

            // * Freeze blocks in a radius around the Player
            else if (isFrostWalkerEnabled(stack)) {
                freezeWater(world, player);
            }
        }
    }
}