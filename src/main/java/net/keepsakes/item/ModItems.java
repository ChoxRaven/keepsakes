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
    // Such a fridge type item
    public static final Item ETERNAL_SNOWFLAKE = register(
            new EternalSnowflake(new Item.Settings()),
            "eternal_snowflake"
    );

    // ? Ordovis's
    // Lets dance!
    public static final Item HF_MURASAMA = register(
            new Item(new Item.Settings()),
            "hf_murasama"
    );

    // ? Chox's
    // Bandit + Scythe
    public static final Item HARVESTERS_SCYTHE = register(
            new HarvestersScythe(GreenScrapMaterial.INSTANCE, new Item.Settings()),
            "harvesters_scythe"
    );

    // Buffs when out in the sun
    public static final Item RADIANT_VIRTUE = register(
            new Item(new Item.Settings()),
            "radiant_virtue"
    );

    // ? Star's
    // LEAN
    public static final Item CHRYSALIS_OF_ETERNITY = register(
            new ChrysalisOfEternity(new Item.Settings()),
            "chrysalis_of_eternity"
    );

    // ? Kix's
    // Wing Stance Aura
    public static final Item MILADY_GREATSWORD = register(
            new Item(new Item.Settings()),
            "milday_greatsword"
    );

    // ? Init functions
    public static void initialize() {
    }
}
