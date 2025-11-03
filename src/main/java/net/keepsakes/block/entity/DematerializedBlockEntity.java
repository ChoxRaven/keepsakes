package net.keepsakes.block.entity;

import net.keepsakes.Keepsakes;
import net.keepsakes.index.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DematerializedBlockEntity extends BlockEntity {
    private BlockState originalBlockState;
    private long creationTime;
    private static final long DURATION_TICKS = 300; // 15 seconds

    public DematerializedBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DEMATERIALIZED_BLOCK_ENTITY, pos, state);
        this.creationTime = world != null ? world.getTime() : 0;
    }

    public void setOriginalBlockState(BlockState originalBlockState) {
        this.originalBlockState = originalBlockState;
        this.creationTime = world != null ? world.getTime() : 0;
        markDirty();

        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }

    public BlockState getOriginalBlockState() {
        return this.originalBlockState;
    }

    public long getCreationTime() {
        return this.creationTime;
    }

    public static long getDurationTicks() {
        return DURATION_TICKS;
    }

    public boolean restoreOriginalBlock() {
        if (world == null || world.isClient || originalBlockState == null) {
            return false;
        }

        try {
            world.setBlockState(pos, originalBlockState);
            Keepsakes.LOGGER.debug("Restored block at {} to original state: {}", pos, originalBlockState);
            return true;
        } catch (Exception e) {
            Keepsakes.LOGGER.error("Failed to restore block at {}: {}", pos, e.getMessage());
            return false;
        }
    }

    public boolean shouldAutoRestore() {
        return world != null &&
                originalBlockState != null &&
                world.getTime() - creationTime >= DURATION_TICKS;
    }

    public static void tick(World world, BlockPos pos, BlockState state, DematerializedBlockEntity blockEntity) {
        if (world.isClient) return;

        // Spawn particles
        Random random = blockEntity.getWorld().getRandom();

        if (random.nextFloat() < 0.8f) {
            ((ServerWorld) world).spawnParticles(
                    ParticleTypes.SOUL, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    1, 0.25, 0.25, 0.25, 0.0
            );
        }

        // Restore after 15s
        if (blockEntity.shouldAutoRestore()) {
            blockEntity.restoreOriginalBlock();
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (originalBlockState != null) {
            nbt.putInt("OriginalState", Block.getRawIdFromState(originalBlockState));
        }
        nbt.putLong("CreationTime", creationTime);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains("OriginalState")) {
            originalBlockState = Block.getStateFromRawId(nbt.getInt("OriginalState"));
        }
        creationTime = nbt.getLong("CreationTime");
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}