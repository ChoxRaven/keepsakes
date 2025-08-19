package net.keepsakes.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.keepsakes.Keepsakes;
import net.keepsakes.item.ModItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    // ! TEMP LOGGER
    private static final Logger LOGGER = LoggerFactory.getLogger(Keepsakes.MOD_ID);

    @ModifyVariable(
            method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At(value = "HEAD"),
            argsOnly = true
    )
    public BakedModel renderItem(BakedModel bakedModel, @Local(argsOnly = true) ItemStack stack, @Local(argsOnly = true) ModelTransformationMode renderMode) {
        if (stack.getItem() == ModItems.HARVESTERS_SCYTHE
                && (renderMode == ModelTransformationMode.GUI
                || renderMode == ModelTransformationMode.GROUND
                || renderMode == ModelTransformationMode.FIXED)) {

            // Use the inventory variant for GUI/ground/fixed
            ModelIdentifier modelId = ModelIdentifier.ofInventoryVariant(
                    Identifier.of(Keepsakes.MOD_ID, "harvesters_scythe")
            );
            BakedModel customModel = MinecraftClient.getInstance().getBakedModelManager().getModel(modelId);

            // Fallback if not found
            return customModel != null ? customModel : bakedModel;
        }

        return bakedModel;
    }

    @ModifyVariable(
            method = "getModel",
            at = @At(value = "STORE"),
            ordinal = 1
    )
    public BakedModel getHeldItemModelMixin(BakedModel bakedModel, @Local(argsOnly = true) ItemStack stack) {
        if (stack.getItem() == ModItems.HARVESTERS_SCYTHE) {
            // Use inventory variant here too
            ModelIdentifier modelId = ModelIdentifier.ofInventoryVariant(
                    Identifier.of(Keepsakes.MOD_ID, "harvesters_scythe_handheld")
            );
            BakedModel customModel = MinecraftClient.getInstance().getBakedModelManager().getModel(modelId);

            return customModel != null ? customModel : bakedModel;
        }

        return bakedModel;
    }
}
