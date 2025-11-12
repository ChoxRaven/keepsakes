// ModBlocks.java
package net.keepsakes.index;

import net.keepsakes.Keepsakes;
import net.keepsakes.block.*;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModBlocks {
    Map<Block, Identifier> BLOCKS = new LinkedHashMap<>();

    Block DEMATERIALIZED_BLOCK = create("dematerialized_block",
            new DematerializedBlock(AbstractBlock.Settings.create()));

    static <T extends Block> T create(String name, T block) {
        Identifier id = Identifier.of(Keepsakes.MOD_ID, name);
        BLOCKS.put(block, id);
        return block;
    }

    static void initialize() {
        BLOCKS.forEach((block, id) -> Registry.register(Registries.BLOCK, id, block));
    }
}