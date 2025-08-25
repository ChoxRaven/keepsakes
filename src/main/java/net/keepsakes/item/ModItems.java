package net.keepsakes.item;

import net.keepsakes.Keepsakes;
import net.keepsakes.item.custom.ChrysalisOfEternity;
import net.keepsakes.item.custom.EternalSnowflake;
import net.keepsakes.item.custom.HarvestersScythe;
import net.keepsakes.material.custom.GreenScrapMaterial;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    // ? Helper Methods
    public static Item register(Item item, String id) {
        // * Create the identifier for the item.
        Identifier itemID = Identifier.of(Keepsakes.MOD_ID, id);

        // * Register the item.
        Item registeredItem = Registry.register(Registries.ITEM, itemID, item);

        // * Return the registered item
        return registeredItem;
    }

    // ? Items
    // ? Miera's
    public static final Item ETERNAL_SNOWFLAKE = register(
            new EternalSnowflake(new Item.Settings()),
            "eternal_snowflake"
    );

    // ? Chox's
    public static final Item HARVESTERS_SCYTHE = register(
            new HarvestersScythe(GreenScrapMaterial.INSTANCE, new Item.Settings()),
            "harvesters_scythe"
    );

    // ? Star's
    // Placeholder
    public static final Item CHRYSALIS_OF_ETERNITY = register(
            new ChrysalisOfEternity(new Item.Settings()),
            "chrysalis_of_eternity"
    );

    // ? Init functions
    public static void initialize() {
    }
}
