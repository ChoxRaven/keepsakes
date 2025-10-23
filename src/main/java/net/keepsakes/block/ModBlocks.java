// ModBlocks.java
package net.keepsakes.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.keepsakes.Keepsakes;
import net.keepsakes.block.custom.DematerializedBlock;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block DEMATERIALIZED_BLOCK = new DematerializedBlock(
            FabricBlockSettings.create()
    );

    public static void registerBlocks() {
        Registry.register(Registries.BLOCK,
                Identifier.of(Keepsakes.MOD_ID, "dematerialized_block"),
                DEMATERIALIZED_BLOCK
        );
    }
}