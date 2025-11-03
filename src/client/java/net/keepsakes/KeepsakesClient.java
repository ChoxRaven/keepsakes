package net.keepsakes;

import net.fabricmc.api.ClientModInitializer;

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