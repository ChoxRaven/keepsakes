package net.keepsakes.block.custom;

import com.mojang.serialization.MapCodec;
import net.keepsakes.block.entity.ModBlockEntities;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DematerializedBlock extends BlockWithEntity {
    public DematerializedBlock(Settings settings) {
        super(settings
                .nonOpaque()
                .noCollision()
                .strength(-1.0f, 3600000.0f)
                .dropsNothing()
        );
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(DematerializedBlock::new);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DematerializedBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.DEMATERIALIZED_BLOCK_ENTITY, DematerializedBlockEntity::tick);
    }
}