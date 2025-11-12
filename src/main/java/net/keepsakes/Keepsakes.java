package net.keepsakes;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.keepsakes.index.ModBlocks;
import net.keepsakes.index.ModBlockEntities;
import net.keepsakes.index.*;
import net.keepsakes.networking.ModNetworking;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Keepsakes implements ModInitializer {
	public static final String MOD_ID = "keepsakes";

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
		ModNetworking.initialize();

		ModBlocks.initialize();
		ModBlockEntities.initialize();
		ModSounds.initialize();
		ModItems.initialize();
        ModTextEffects.initialize();

		LOGGER.info("Keepsakes mod initialized successfully!");
	}
}