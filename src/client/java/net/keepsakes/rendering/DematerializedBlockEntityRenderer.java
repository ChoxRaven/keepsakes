package net.keepsakes.rendering;

import net.keepsakes.block.entity.DematerializedBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

public class DematerializedBlockEntityRenderer implements BlockEntityRenderer<DematerializedBlockEntity> {

    public DematerializedBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(DematerializedBlockEntity blockEntity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
    }
}