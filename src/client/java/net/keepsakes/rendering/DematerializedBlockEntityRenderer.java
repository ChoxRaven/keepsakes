package net.keepsakes.rendering;

import net.keepsakes.block.custom.DematerializedBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DematerializedBlockEntityRenderer implements BlockEntityRenderer<DematerializedBlockEntity> {

    public DematerializedBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(DematerializedBlockEntity blockEntity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {

        World world = blockEntity.getWorld();
        BlockPos pos = blockEntity.getPos();

        if (world == null) return;

        // Calculate time-based effects
        long currentTime = world.getTime();
        long creationTime = blockEntity.getCreationTime();
        float progress = Math.min(1.0f, (float) (currentTime - creationTime) / DematerializedBlockEntity.getDurationTicks());

        // Pulsing alpha effect
        float alpha = 0.5f + 0.3f * (float) Math.sin(world.getTime() * 0.1f);

        // Color that changes over time (blue -> purple -> red)
        float red = progress * 0.8f;
        float green = 0.2f;
        float blue = 0.8f - (progress * 0.6f);

        renderCustomOutline(matrices, vertexConsumers, pos, red, green, blue, alpha);
    }

    private void renderCustomOutline(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                     BlockPos pos, float red, float green, float blue, float alpha) {

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());
        MatrixStack.Entry entry = matrices.peek();

        float x = pos.getX();
        float y = pos.getY();
        float z = pos.getZ();

        // Define the 8 corners of the slightly enlarged block
        float minX = x - 0.005f;
        float minY = y - 0.005f;
        float minZ = z - 0.005f;
        float maxX = x + 1.005f;
        float maxY = y + 1.005f;
        float maxZ = z + 1.005f;

        // Draw the 12 edges of the cube
        drawLine(vertexConsumer, entry, minX, minY, minZ, maxX, minY, minZ, red, green, blue, alpha);
        drawLine(vertexConsumer, entry, minX, maxY, minZ, maxX, maxY, minZ, red, green, blue, alpha);
        drawLine(vertexConsumer, entry, minX, minY, maxZ, maxX, minY, maxZ, red, green, blue, alpha);
        drawLine(vertexConsumer, entry, minX, maxY, maxZ, maxX, maxY, maxZ, red, green, blue, alpha);

        drawLine(vertexConsumer, entry, minX, minY, minZ, minX, maxY, minZ, red, green, blue, alpha);
        drawLine(vertexConsumer, entry, maxX, minY, minZ, maxX, maxY, minZ, red, green, blue, alpha);
        drawLine(vertexConsumer, entry, minX, minY, maxZ, minX, maxY, maxZ, red, green, blue, alpha);
        drawLine(vertexConsumer, entry, maxX, minY, maxZ, maxX, maxY, maxZ, red, green, blue, alpha);

        drawLine(vertexConsumer, entry, minX, minY, minZ, minX, minY, maxZ, red, green, blue, alpha);
        drawLine(vertexConsumer, entry, maxX, minY, minZ, maxX, minY, maxZ, red, green, blue, alpha);
        drawLine(vertexConsumer, entry, minX, maxY, minZ, minX, maxY, maxZ, red, green, blue, alpha);
        drawLine(vertexConsumer, entry, maxX, maxY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
    }

    private void drawLine(VertexConsumer vertexConsumer, MatrixStack.Entry entry,
                          float x1, float y1, float z1, float x2, float y2, float z2,
                          float red, float green, float blue, float alpha) {
        // Calculate normal for the line (perpendicular to the line direction)
        float dx = x2 - x1;
        float dy = y2 - y1;
        float dz = z2 - z1;

        // Use a simple default normal (up vector) since line normals are complex
        float normalX = 0.0f;
        float normalY = 1.0f;
        float normalZ = 0.0f;

        // Transform the normal using the normal matrix
        float transformedNormalX = normalX;
        float transformedNormalY = normalY;
        float transformedNormalZ = normalZ;

        vertexConsumer.vertex(entry.getPositionMatrix(), x1, y1, z1)
                .color(red, green, blue, alpha)
                .normal(entry, transformedNormalX, transformedNormalY, transformedNormalZ);

        vertexConsumer.vertex(entry.getPositionMatrix(), x2, y2, z2)
                .color(red, green, blue, alpha)
                .normal(entry, transformedNormalX, transformedNormalY, transformedNormalZ);
    }
}