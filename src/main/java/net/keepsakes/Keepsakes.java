package net.keepsakes;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.keepsakes.item.ModItems;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Keepsakes implements ModInitializer {
	public static final String MOD_ID = "keepsakes";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final SimpleParticleType AMBITION_HALO_PARTICLE = FabricParticleTypes.simple();

    public static void initializeParticles() {
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(MOD_ID, "ambition_halo_particle"), AMBITION_HALO_PARTICLE);
    }

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		// * Initialize Items
		LOGGER.info("Loading Items...");
		ModItems.initialize();
		LOGGER.info("Finished loading items!");

        LOGGER.info("Loading Particles...");
        initializeParticles();
        LOGGER.info("Finished loading particles!");
	}
}