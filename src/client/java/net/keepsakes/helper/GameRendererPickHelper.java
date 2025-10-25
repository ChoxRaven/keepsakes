package net.keepsakes.helper;

import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

// Implemented from https://github.com/Fuzss/cutthrough/blob/main/1.21.1/Common/src/main/java/fuzs/cutthrough/client/helper/GameRendererPickHelper.java

public class GameRendererPickHelper {
    public static HitResult pick(Entity entity, double pickRange, float partialTick) {
        Vec3d eyePosition = entity.getEyePos();
        Vec3d viewVector = entity.getRotationVec(partialTick);
        Vec3d vec3 = eyePosition.add(viewVector.x * pickRange, viewVector.y * pickRange, viewVector.z * pickRange);
        // pick from collider as opposed to outline
        return entity.getWorld().raycast(new RaycastContext(eyePosition, vec3, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity));
    }
}