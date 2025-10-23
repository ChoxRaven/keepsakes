package net.keepsakes.item.logic;

import net.keepsakes.Keepsakes;
import net.keepsakes.block.ModBlocks;
import net.keepsakes.block.custom.DematerializedBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DematerializerLogic {
    private static final double MAX_DEMATERIALIZE_DISTANCE = 10.0;

    public static void handleLeftClick(World world, PlayerEntity player, ItemStack stack) {
        Keepsakes.LOGGER.info("SERVER: DematerializerLogic - Processing left click for {}", player.getName().getString());

        if (world.isClient) {
            return;
        }

        // Play sound effect
        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP,
                SoundCategory.PLAYERS, 1.0f, 1.0f);

        // Process the dematerialization logic
        processDematerialization(world, player, stack);
    }

    private static void processDematerialization(World world, PlayerEntity player, ItemStack stack) {
        Keepsakes.LOGGER.info("SERVER: Processing dematerialization logic");

        // Perform raycast to find the targeted block
        HitResult hitResult = raycast(world, player, false);

        // Check if it's a block hit and cast to BlockHitResult
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            BlockPos targetPos = blockHitResult.getBlockPos();
            Direction side = blockHitResult.getSide();

            Keepsakes.LOGGER.info("SERVER: Targeting block at {} on side {}", targetPos, side);

            // Get the original block state
            BlockState originalState = world.getBlockState(targetPos);
            Block originalBlock = originalState.getBlock();

            // Skip if it's air, our temporary block, or unbreakable
            if (originalState.isAir() ||
                    originalBlock == ModBlocks.DEMATERIALIZED_BLOCK ||
                    originalState.getHardness(world, targetPos) < 0) {
                Keepsakes.LOGGER.info("SERVER: Cannot dematerialize this block type");
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.PLAYERS, 0.5f, 1.5f);
                return;
            }

            // Check if block is within reach distance
            if (!isWithinReach(player, targetPos, MAX_DEMATERIALIZE_DISTANCE)) {
                Keepsakes.LOGGER.info("SERVER: Block is too far away");
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BLOCK_NOTE_BLOCK_BASS, SoundCategory.PLAYERS, 0.5f, 0.5f);
                return;
            }

            try {
                // Store the original block data before replacement
                Keepsakes.LOGGER.info("SERVER: Dematerializing block {} at {}",
                        Block.getRawIdFromState(originalState), targetPos);

                // Replace with our temporary block
                world.setBlockState(targetPos, ModBlocks.DEMATERIALIZED_BLOCK.getDefaultState());

                // Set the original block data in the block entity
                if (world.getBlockEntity(targetPos) instanceof DematerializedBlockEntity blockEntity) {
                    blockEntity.setOriginalBlockState(originalState);
                    Keepsakes.LOGGER.info("SERVER: Successfully stored original block data");

                    // Play success sound at the block location
                    world.playSound(null, targetPos,
                            SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK,
                            SoundCategory.BLOCKS, 0.8f, 1.2f);

                } else {
                    Keepsakes.LOGGER.error("SERVER: Failed to get block entity after replacement!");
                    // Restore the original block if we failed
                    world.setBlockState(targetPos, originalState);
                }

            } catch (Exception e) {
                Keepsakes.LOGGER.error("SERVER: Error during dematerialization", e);
                // Try to restore the original block on error
                world.setBlockState(targetPos, originalState);
            }

        } else {
            Keepsakes.LOGGER.info("SERVER: No block targeted - looking at air or entity");
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BLOCK_NOTE_BLOCK_HAT, SoundCategory.PLAYERS, 0.5f, 1.5f);
        }
    }

    // Return HitResult and let the caller handle the type checking
    private static HitResult raycast(World world, PlayerEntity player, boolean includeFluids) {
        return player.raycast(MAX_DEMATERIALIZE_DISTANCE, 0.0f, includeFluids);
    }

    private static boolean isWithinReach(PlayerEntity player, BlockPos pos, double maxDistance) {
        Vec3d playerPos = player.getEyePos();
        Vec3d blockPos = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        double distance = playerPos.distanceTo(blockPos);
        return distance <= maxDistance;
    }
}