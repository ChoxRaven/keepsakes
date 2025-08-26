package net.keepsakes;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.keepsakes.particle.custom.AmbitionHaloParticle;
import net.minecraft.client.particle.EndRodParticle;

public class KeepsakesClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
        ParticleFactoryRegistry.getInstance().register(Keepsakes.AMBITION_HALO_PARTICLE, AmbitionHaloParticle.Factory::new);
    }
}