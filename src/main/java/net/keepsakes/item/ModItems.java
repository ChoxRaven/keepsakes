package net.keepsakes.item;

import net.keepsakes.Keepsakes;
import net.keepsakes.item.custom.*;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    // ? Helper Methods
    public static Item register(Item item, String id) {
        // * Create the identifier for the item.
        Identifier itemID = Identifier.of(Keepsakes.MOD_ID, id);

        // * Return the registered item
        return Registry.register(Registries.ITEM, itemID, item);
    }

    // ? Items
    // ? Miera's
    // Such a fridge type item
    public static final Item ETERNAL_SNOWFLAKE = register(
            new EternalSnowflakeItem(new Item.Settings()),
            "eternal_snowflake"
    );

    // ? Ordovis's
    // Lets dance!
    public static final Item HF_MURASAMA = register(
            new HFMurasamaItem(ToolMaterials.NETHERITE, new Item.Settings()),
            "hf_murasama"
    );

    // ? Chox's
    // Bandit + Scythe
    public static final Item HARVESTERS_SCYTHE = register(
            new HarvestersScytheItem(ToolMaterials.NETHERITE, new Item.Settings()),
            "harvesters_scythe"
    );

    // Buffs when out in the sun
    public static final Item RADIANT_VIRTUE = register(
            new Item(new Item.Settings()),
            "radiant_virtue"
    );

    // Buffs based on the stats of the current biome (hot, cold, humid, etc.)
    public static final Item ASPECT_OF_THE_ELEMENTS = register(
            new Item(new Item.Settings()),
            "aspect_of_the_elements"
    );

    // The finales
    public static final Item DEMATERIALIZER = register(
            new DematerializerItem(new Item.Settings()),
            "dematerializer"
    );

    // ? Star's
    // LEAN
    public static final Item CHRYSALIS_OF_ETERNITY = register(
            new ChrysalisOfEternityItem(new Item.Settings()),
            "chrysalis_of_eternity"
    );

    // ? Litsu's
    // Spy TF2
    public static final Item DEADRINGER = register(
            new Item(new Item.Settings()),
            "deadringer"
    );

    // Schizoid
    public static final Item LUCY_THE_AXE = register(
            new Item(new Item.Settings()),
            "lucy_the_axe"
    );

    // ? Kix's
    // Aurafarming weapon
    public static final Item MILADY_GREATSWORD = register(
            new Item(new Item.Settings()),
            "milday_greatsword"
    );

    // ? Init functions
    public static void initialize() {
    }
}
