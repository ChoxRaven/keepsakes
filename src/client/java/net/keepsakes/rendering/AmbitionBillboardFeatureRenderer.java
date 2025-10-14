    package net.keepsakes.rendering;

    import net.keepsakes.Keepsakes;
    import net.minecraft.client.MinecraftClient;
    import net.minecraft.client.render.Camera;
    import net.minecraft.client.render.RenderLayer;
    import net.minecraft.client.render.VertexConsumer;
    import net.minecraft.client.render.VertexConsumerProvider;
    import net.minecraft.client.render.entity.feature.FeatureRenderer;
    import net.minecraft.client.render.entity.feature.FeatureRendererContext;
    import net.minecraft.client.util.math.MatrixStack;
    import net.minecraft.entity.Entity;
    import net.minecraft.util.Identifier;
    import net.minecraft.util.math.RotationAxis;
    import org.joml.Vector3f;

    public class AmbitionBillboardFeatureRenderer extends FeatureRenderer {
        private static final Identifier TEXTURE = Identifier.of(Keepsakes.MOD_ID, "textures/particle/ambition_halo_texture.png");

        public AmbitionBillboardFeatureRenderer(FeatureRendererContext context) {
            super(context);
        }

        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Entity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
            // TODO : Implement function to check when this should be drawn on players
            boolean shouldRender = true;

            if(shouldRender) {
                int overlay = 0;
                matrices.push();

                // Translate to the player's head
                matrices.translate(0.0D, entity.getHeight() - 2.2f, 0.0D);

                // Client camera
                Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();

                // TODO : counteract the playerEntity's body rotation, or use a different rendering method

                // Apply rotation
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw()));

                // Draw the billboard
                matrices.scale(0.5f, 0.5f, 0.5f);

                VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(TEXTURE));

                Vector3f normal = new Vector3f(0.0f, 0.0f, 1.0f);
                normal.mul(matrices.peek().getNormalMatrix());

                vertexConsumer.vertex(matrices.peek().getPositionMatrix(), -1.0f, -1.0f, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).texture(0.0f, 1.0f).overlay(overlay).light(light).normal(normal.x, normal.y, normal.z);
                vertexConsumer.vertex(matrices.peek().getPositionMatrix(), -1.0f, 1.0f, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).texture(0.0f, 0.0f).overlay(overlay).light(light).normal(normal.x, normal.y, normal.z);
                vertexConsumer.vertex(matrices.peek().getPositionMatrix(), 1.0f, 1.0f, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).texture(1.0f, 0.0f).overlay(overlay).light(light).normal(normal.x, normal.y, normal.z);
                vertexConsumer.vertex(matrices.peek().getPositionMatrix(), 1.0f, -1.0f, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).texture(1.0f, 1.0f).overlay(overlay).light(light).normal(normal.x, normal.y, normal.z);

                matrices.pop();
            }


        }
    }
