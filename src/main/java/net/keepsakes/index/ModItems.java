package net.keepsakes.index;

import net.keepsakes.Keepsakes;
import net.keepsakes.item.*;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModItems {
    Map<Item, Identifier> ITEMS = new LinkedHashMap<>();

    // ? Miera
    Item ETERNAL_SNOWFLAKE = create("eternal_snowflake", new EternalSnowflakeItem(new Item.Settings())); // Fitting.

    // ? Chox
    Item HARVESTERS_SCYTHE = create("harvesters_scythe", new HarvestersScytheItem(ToolMaterials.NETHERITE, new Item.Settings())); // +8 +8 +8 +8
    Item HUNTERS_REGARDS = create("hunters_regards", new HuntersRegardsItem(new Item.Settings())); // What HP?
    Item RADIANT_VIRTUE = create("radiant_virtue", new Item(new Item.Settings())); // Buffs when out in the sun
    Item ASPECT_OF_THE_ELEMENTS = create("aspect_of_the_elements", new Item(new Item.Settings())); // Buffs based on the stats of the current biome (hot, cold, humid, etc.)
    Item DEMATERIALIZER = create("dematerializer", new DematerializerItem(new Item.Settings())); // The Finales

    // ? Ordovis
    Item HF_MURASAMA = create("hf_murasama", new HFMurasamaItem(ToolMaterials.NETHERITE, new Item.Settings())); // Let's dance!

    // ? Nidzulaa
    Item DATA_RESHAPER = create("data_reshaper", new Item(new Item.Settings())); // The Finales Pt. 2

    // ? Star
    Item CHRYSALIS_OF_ETERNITY = create("chrysalis_of_eternity", new ChrysalisOfEternityItem(new Item.Settings())); // Lean

    // ? Litsu
    Item DEADRINGER = create("deadringer", new Item(new Item.Settings())); // Spy TF2
    Item LUCY_THE_AXE = create("lucy_the_axe", new Item(new Item.Settings())); // Schizoid

    // ? Kix
    Item MILADY_GREATSWORD = create("milday_greatsword", new Item(new Item.Settings())); // Aurafarming weapon

    static <T extends Item> T create(String name, T item) {
        Identifier id = Identifier.of(Keepsakes.MOD_ID, name);
        ITEMS.put(item, id);

        return item;
    }

    public static void initialize() {
        ITEMS.forEach((item, id) -> Registry.register(Registries.ITEM, id, item));
    }
}
