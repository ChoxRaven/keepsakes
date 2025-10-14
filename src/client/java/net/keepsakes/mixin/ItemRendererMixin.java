package net.keepsakes.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.keepsakes.Keepsakes;
import net.keepsakes.item.ModItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Unique
    private FabricBakedModel registerItemModel(BakedModel bakedModel, @Local(argsOnly = true) ItemStack stack, @Local(argsOnly = true) ModelTransformationMode renderMode, Item item, String path) {
        if (stack.getItem() == item
                && (renderMode == ModelTransformationMode.GUI
                || renderMode == ModelTransformationMode.GROUND
                || renderMode == ModelTransformationMode.FIXED)) {

            // Use the inventory variant for GUI/ground/fixed
            ModelIdentifier modelId = ModelIdentifier.ofInventoryVariant(
                    Identifier.of(Keepsakes.MOD_ID, path)
            );
            BakedModel customModel = MinecraftClient.getInstance().getBakedModelManager().getModel(modelId);

            // Fallback if not found
            return customModel != null ? customModel : bakedModel;
        }
        return null;
    }

    @Unique
    private FabricBakedModel registerHeldItemModel(BakedModel bakedModel, @Local(argsOnly = true) ItemStack stack, Item item, String path) {
        if (stack.getItem() == item) {
            ModelIdentifier modelId = ModelIdentifier.ofInventoryVariant(
                    Identifier.of(Keepsakes.MOD_ID, path)
            );
            BakedModel customModel = MinecraftClient.getInstance().getBakedModelManager().getModel(modelId);

            return customModel != null ? customModel : bakedModel;
        }
        return null;
    }

    @ModifyVariable(
            method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At(value = "HEAD"),
            argsOnly = true
    )
    public BakedModel renderItem(BakedModel bakedModel, @Local(argsOnly = true) ItemStack stack, @Local(argsOnly = true) ModelTransformationMode renderMode) {
        registerItemModel(bakedModel, stack, renderMode, ModItems.HARVESTERS_SCYTHE, "harvesters_scythe");
        registerItemModel(bakedModel, stack, renderMode, ModItems.HF_MURASAMA, "hf_murasama");

        return bakedModel;
    }

    @ModifyVariable(
            method = "getModel",
            at = @At(value = "STORE"),
            ordinal = 1
    )

    public BakedModel getHeldItemModelMixin(BakedModel bakedModel, @Local(argsOnly = true) ItemStack stack) {
        registerHeldItemModel(bakedModel, stack, ModItems.HARVESTERS_SCYTHE, "harvesters_scythe_handheld");
        registerHeldItemModel(bakedModel, stack, ModItems.HF_MURASAMA, "hf_murasama_handheld");

        return bakedModel;
    }
}
