package net.keepsakes.item.custom;

import net.minecraft.item.Item;

public class Dematerializer extends Item {
    // * Item Settings
    public Dematerializer(Settings settings) {
        super(settings
                .maxCount(1)
                .fireproof()
        );
    }
}