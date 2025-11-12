package net.keepsakes.index;

import net.keepsakes.Keepsakes;
import net.keepsakes.block.entity.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public interface ModBlockEntities {
    Map<BlockEntityType<?>, Identifier> BLOCK_ENTITY_TYPES = new LinkedHashMap<>();

    BlockEntityType<DematerializedBlockEntity> DEMATERIALIZED_BLOCK_ENTITY = create(
            "dematerialized_block_entity",
            () -> BlockEntityType.Builder.create(
                    DematerializedBlockEntity::new,
                    ModBlocks.DEMATERIALIZED_BLOCK
            ).build(null)
    );

    static <T extends BlockEntityType<?>> T create(String name, Supplier<T> blockEntityTypeSupplier) {
        T blockEntityType = blockEntityTypeSupplier.get();
        Identifier id = Identifier.of(Keepsakes.MOD_ID, name);
        BLOCK_ENTITY_TYPES.put(blockEntityType, id);
        return blockEntityType;
    }

    static void initialize() {
        BLOCK_ENTITY_TYPES.forEach(((blockEntityType, id) ->
                Registry.register(Registries.BLOCK_ENTITY_TYPE, id, blockEntityType))
        );
    }
}