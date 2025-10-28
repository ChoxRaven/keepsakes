package net.keepsakes;

import net.fabricmc.api.ClientModInitializer;
import net.keepsakes.block.entity.ModBlockEntities;
import net.keepsakes.rendering.DematerializedBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class KeepsakesClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
        //BlockEntityRendererFactories.register(ModBlockEntities.DEMATERIALIZED_BLOCK_ENTITY, DematerializedBlockEntityRenderer::new);
//        LivingEntityFeatureRendererRegistrationCallback.EVENT.register(((entityType, livingEntityRenderer, registrationHelper, context) -> {
//            if (livingEntityRenderer instanceof PlayerEntityRenderer) {
//                registrationHelper.register(new AmbitionBillboardFeatureRenderer((PlayerEntityRenderer) livingEntityRenderer));
//            }
//        }));
    }
}