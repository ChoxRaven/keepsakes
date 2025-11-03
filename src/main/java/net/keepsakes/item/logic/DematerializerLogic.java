package net.keepsakes.item.logic;

import net.keepsakes.Keepsakes;
import net.keepsakes.index.ModBlocks;
import net.keepsakes.block.entity.DematerializedBlockEntity;
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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class DematerializerLogic {
    private static final double MAX_DEMATERIALIZE_DISTANCE = 10.0;

    public static void handleLeftClick(World world, PlayerEntity player, ItemStack stack) {
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

    public static void handleRightClick(World world, PlayerEntity player, BlockPos targetPos) {
        if (world.isClient) {
            return;
        }

        if (world.getBlockState(targetPos).getBlock() == ModBlocks.DEMATERIALIZED_BLOCK) {

            // Check if block entity exists
            if (!(world.getBlockEntity(targetPos) instanceof DematerializedBlockEntity)) {
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.PLAYERS, 0.5f, 1.5f);
                return;
            }

            // Revert this block and all connected dematerialized blocks
            int revertedCount = revertConnectedBlocks(world, targetPos);

            if (revertedCount > 0) {

                // Play success sound
                world.playSound(null, targetPos,
                        SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME,
                        SoundCategory.BLOCKS, 1.0f, 1.0f);
            } else {
                // Play failure sound
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BLOCK_NOTE_BLOCK_BASS, SoundCategory.PLAYERS, 0.5f, 0.5f);
            }
        } else {
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BLOCK_NOTE_BLOCK_HAT, SoundCategory.PLAYERS, 0.5f, 1.5f);
        }
    }

    private static int revertConnectedBlocks(World world, BlockPos startPos) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        int revertedCount = 0;

        // Start with the clicked block
        queue.add(startPos);
        visited.add(startPos);

        // Use BFS to find all connected dematerialized blocks
        while (!queue.isEmpty() && revertedCount < 1000) {
            BlockPos currentPos = queue.poll();

            // Revert this block using the block entity's restoration function
            if (revertSingleBlock(world, currentPos)) {
                revertedCount++;
            } else {
            }

            // Check all 6 adjacent positions
            for (BlockPos neighborPos : getNeighbors(currentPos)) {
                if (!visited.contains(neighborPos) &&
                        world.getBlockState(neighborPos).getBlock() == ModBlocks.DEMATERIALIZED_BLOCK) {
                    queue.add(neighborPos);
                    visited.add(neighborPos);
                }
            }
        }

        return revertedCount;
    }

    private static BlockPos[] getNeighbors(BlockPos pos) {
        return new BlockPos[]{
                pos.up(),
                pos.down(),
                pos.north(),
                pos.south(),
                pos.east(),
                pos.west()
        };
    }

    private static boolean revertSingleBlock(World world, BlockPos pos) {
        try {

            if (world.getBlockEntity(pos) instanceof DematerializedBlockEntity blockEntity) {
                // Use the block entity's dedicated restoration function
                return blockEntity.restoreOriginalBlock();
            } else {
                Keepsakes.LOGGER.error("SERVER: No block entity found at {}", pos);
            }
        } catch (Exception e) {
            Keepsakes.LOGGER.error("SERVER: Error reverting block at {}", pos, e);
        }

        return false;
    }

    private static void processDematerialization(World world, PlayerEntity player, ItemStack stack) {
        HitResult hitResult = raycast(world, player, false);

        // Check if it's a block hit and cast to BlockHitResult
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            BlockPos targetPos = blockHitResult.getBlockPos();
            Direction side = blockHitResult.getSide();

            // Get all surface blocks in 3x3 area
            Set<BlockPos> surfaceBlocks = getSurfaceBlocks(world, targetPos, side);

            if (surfaceBlocks.isEmpty()) {
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.PLAYERS, 0.5f, 1.5f);
                return;
            }

            int successCount = 0;

            // Process each surface block
            for (BlockPos pos : surfaceBlocks) {
                if (dematerializeSingleBlock(world, player, pos)) {
                    successCount++;
                }
            }

            if (successCount > 0) {
                // Play success sound at the center location
                world.playSound(null, targetPos,
                        SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK,
                        SoundCategory.BLOCKS, 1.0f, 1.2f);
            } else {
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BLOCK_NOTE_BLOCK_BASS, SoundCategory.PLAYERS, 0.5f, 0.5f);
            }

        } else {
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BLOCK_NOTE_BLOCK_HAT, SoundCategory.PLAYERS, 0.5f, 1.5f);
        }
    }

    private static Set<BlockPos> getSurfaceBlocks(World world, BlockPos centerPos, Direction face) {
        Set<BlockPos> surfaceBlocks = new HashSet<>();

        // Get the two perpendicular directions to form our 3x3 grid
        Direction[] perpendicularDirs = getPerpendicularDirections(face);

        // Generate 3x3 grid in the plane of the face
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                BlockPos surfacePos = centerPos
                        .offset(perpendicularDirs[0], i)
                        .offset(perpendicularDirs[1], j);

                // Simplified: just check if the block itself is valid and the space in front is empty
                if (isValidSurfaceBlock(world.getBlockState(surfacePos)) &&
                        isSpaceEmpty(world, surfacePos, face)) {
                    surfaceBlocks.add(surfacePos);
                }
            }
        }

        return surfaceBlocks;
    }

    private static Direction[] getPerpendicularDirections(Direction face) {
        return switch (face) {
            case UP, DOWN -> new Direction[]{Direction.NORTH, Direction.EAST};
            case NORTH, SOUTH -> new Direction[]{Direction.UP, Direction.EAST};
            case EAST, WEST -> new Direction[]{Direction.UP, Direction.NORTH};
        };
    }

    private static boolean isSpaceEmpty(World world, BlockPos pos, Direction face) {
        BlockPos inFront = pos.offset(face);
        BlockState inFrontState = world.getBlockState(inFront);
        return inFrontState.isAir() || inFrontState.isReplaceable();
    }

    private static boolean isValidSurfaceBlock(BlockState state) {
        return !state.isAir() &&
                state.getBlock() != ModBlocks.DEMATERIALIZED_BLOCK &&
                state.getHardness(null, null) >= 0; // Not unbreakable
    }

    private static boolean dematerializeSingleBlock(World world, PlayerEntity player, BlockPos pos) {
        try {
            BlockState originalState = world.getBlockState(pos);
            Block originalBlock = originalState.getBlock();

            // Skip if it's air, our temporary block, or unbreakable
            if (originalState.isAir() ||
                    originalBlock == ModBlocks.DEMATERIALIZED_BLOCK ||
                    originalState.getHardness(world, pos) < 0) {
                return false;
            }

            // Check if block is within reach distance
            if (!isWithinReach(player, pos, MAX_DEMATERIALIZE_DISTANCE)) {
                return false;
            }

            // Replace with our temporary block
            world.setBlockState(pos, ModBlocks.DEMATERIALIZED_BLOCK.getDefaultState());

            // Set the original block data in the block entity
            if (world.getBlockEntity(pos) instanceof DematerializedBlockEntity blockEntity) {
                blockEntity.setOriginalBlockState(originalState);
                return true;
            } else {
                Keepsakes.LOGGER.error("SERVER: Failed to get block entity after replacement at {}!", pos);
                // Restore the original block if we failed
                world.setBlockState(pos, originalState);
                return false;
            }

        } catch (Exception e) {
            Keepsakes.LOGGER.error("SERVER: Error dematerializing block at {}", pos, e);
            return false;
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