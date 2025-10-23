package net.keepsakes.block.custom;

import net.keepsakes.block.entity.ModBlockEntities;
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
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DematerializedBlockEntity extends BlockEntity {
    private BlockState originalBlockState;
    private long removalTime;
    private static final long DURATION_TICKS = 100; // 5 seconds

    public DematerializedBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DEMATERIALIZED_BLOCK_ENTITY, pos, state);
    }

    public void setOriginalBlockState(BlockState originalBlockState) {
        this.originalBlockState = originalBlockState;
        this.removalTime = world != null ? world.getTime() : 0;
        markDirty();

        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, DematerializedBlockEntity blockEntity) {
        if (world.isClient) return;
        ((ServerWorld) blockEntity.getWorld()).spawnParticles(
                ParticleTypes.SOUL, blockEntity.getPos().getX() + 0.5, blockEntity.getPos().getY() + 0.5, blockEntity.getPos().getZ() + 0.5, 1, 0.25, 0.25, 0.25, 0.0
        );
        if (blockEntity.originalBlockState != null &&
                world.getTime() - blockEntity.removalTime >= DURATION_TICKS) {
            // Restore the original block
            world.setBlockState(pos, blockEntity.originalBlockState);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (originalBlockState != null) {
            nbt.putInt("OriginalState", Block.getRawIdFromState(originalBlockState));
        }
        nbt.putLong("RemovalTime", removalTime);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains("OriginalState")) {
            originalBlockState = Block.getStateFromRawId(nbt.getInt("OriginalState"));
        }
        removalTime = nbt.getLong("RemovalTime");
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