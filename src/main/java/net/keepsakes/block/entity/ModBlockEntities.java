package net.keepsakes.block.entity;

import net.keepsakes.Keepsakes;
import net.keepsakes.block.ModBlocks;
import net.keepsakes.block.custom.DematerializedBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static BlockEntityType<DematerializedBlockEntity> DEMATERIALIZED_BLOCK_ENTITY;

    public static void registerBlockEntities() {
        DEMATERIALIZED_BLOCK_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(Keepsakes.MOD_ID, "dematerialized_block_entity"),
                BlockEntityType.Builder.create(
                        DematerializedBlockEntity::new,
                        ModBlocks.DEMATERIALIZED_BLOCK
                ).build(null)
        );
    }
}