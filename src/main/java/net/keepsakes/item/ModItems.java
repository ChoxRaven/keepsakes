package net.keepsakes.item;

import net.keepsakes.Keepsakes;
import net.keepsakes.item.custom.EternalSnowflake;
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
    public static final Item ETERNAL_SNOWFLAKE = register(
            new EternalSnowflake(new Item.Settings()),
            "eternal_snowflake"
    );

    // ? Init functions
    public static void initialize() {
    }
}
