package net.keepsakes;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.keepsakes.particle.custom.AmbitionHaloParticle;
import net.keepsakes.rendering.AmbitionBillboardFeatureRenderer;
import net.minecraft.client.particle.EndRodParticle;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class KeepsakesClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
//        LivingEntityFeatureRendererRegistrationCallback.EVENT.register(((entityType, livingEntityRenderer, registrationHelper, context) -> {
//            if (livingEntityRenderer instanceof PlayerEntityRenderer) {
//                registrationHelper.register(new AmbitionBillboardFeatureRenderer((PlayerEntityRenderer) livingEntityRenderer));
//            }
//        }));
    }
}