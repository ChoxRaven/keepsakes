package net.keepsakes.index;

import net.keepsakes.Keepsakes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModSounds {
    Map<SoundEvent, Identifier> SOUND_EVENTS = new LinkedHashMap<>();

    SoundEvent HARVESTERS_SCYTHE_CRITICAL = createSoundEvent("item.harvesters_scythe.critical");

    static void initialize() {
        SOUND_EVENTS.keySet().forEach(soundEvent -> Registry.register(Registries.SOUND_EVENT, SOUND_EVENTS.get(soundEvent), soundEvent));
    }

    private static SoundEvent createSoundEvent(String path) {
        Identifier id = Identifier.of(Keepsakes.MOD_ID, path);
        SoundEvent soundEvent = SoundEvent.of(id);
        SOUND_EVENTS.put(soundEvent, id);
        return soundEvent;
    }
}
