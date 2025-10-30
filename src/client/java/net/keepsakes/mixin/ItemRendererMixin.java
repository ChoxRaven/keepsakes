package net.keepsakes.mixin;

import com.llamalad7.mixinextras.sugar.Local;
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

import java.util.HashMap;
import java.util.Map;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Unique
    private static final Map<Item, String> ITEM_MODELS = new HashMap<>();
    @Unique
    private static final Map<Item, String> HANDHELD_MODELS = new HashMap<>();

    static {
        // Register inventory/GUI models
        ITEM_MODELS.put(ModItems.HF_MURASAMA, "hf_murasama");
        ITEM_MODELS.put(ModItems.HARVESTERS_SCYTHE, "harvesters_scythe");

        // Register handheld models
        HANDHELD_MODELS.put(ModItems.HF_MURASAMA, "hf_murasama_handheld");
        HANDHELD_MODELS.put(ModItems.HARVESTERS_SCYTHE, "harvesters_scythe_handheld");
    }

    @Unique
    private BakedModel keepsakes$getCustomModel(BakedModel bakedModel, ItemStack stack, String modelPath) {
        if (modelPath == null) {
            return bakedModel;
        }

        ModelIdentifier modelId = ModelIdentifier.ofInventoryVariant(
                Identifier.of(Keepsakes.MOD_ID, modelPath)
        );

        BakedModel customModel = MinecraftClient.getInstance().getBakedModelManager().getModel(modelId);

        return customModel != null ? customModel : bakedModel;
    }

    @ModifyVariable(
            method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At(value = "HEAD"),
            argsOnly = true
    )
    public BakedModel keepsakes$renderItem(BakedModel bakedModel, @Local(argsOnly = true) ItemStack stack, @Local(argsOnly = true) ModelTransformationMode renderMode) {
        if (renderMode == ModelTransformationMode.GUI
                || renderMode == ModelTransformationMode.GROUND
                || renderMode == ModelTransformationMode.FIXED) {

            String modelPath = ITEM_MODELS.get(stack.getItem());
            if (modelPath != null) {
                return keepsakes$getCustomModel(bakedModel, stack, modelPath);
            }
        }

        return bakedModel;
    }

    @ModifyVariable(
            method = "getModel",
            at = @At(value = "STORE"),
            ordinal = 1
    )
    public BakedModel keepsakes$getHeldItemModel(BakedModel bakedModel, @Local(argsOnly = true) ItemStack stack) {
        String modelPath = HANDHELD_MODELS.get(stack.getItem());
        if (modelPath != null) {
            return keepsakes$getCustomModel(bakedModel, stack, modelPath);
        }

        return bakedModel;
    }
}