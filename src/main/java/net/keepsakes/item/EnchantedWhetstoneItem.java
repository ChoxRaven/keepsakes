package net.keepsakes.item;

import net.keepsakes.Keepsakes;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class EnchantedWhetstoneItem extends Item {
    public EnchantedWhetstoneItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack target;
        ItemStack stack = user.getStackInHand(hand);

        if (hand == Hand.MAIN_HAND) {
            target = user.getStackInHand(Hand.OFF_HAND);
        } else {
            target = user.getStackInHand(Hand.MAIN_HAND);
        }

        if (target.getItem() instanceof SwordItem || target.isOf(Items.NETHERITE_HOE)) {
            NbtComponent customData = target.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
            NbtCompound nbt = customData.copyNbt();

            if (!nbt.contains("EnchantedWhetstoneUsed")) {
                RegistryEntry<Enchantment> sharpnessEntry = world.getRegistryManager()
                        .get(RegistryKeys.ENCHANTMENT)
                        .getEntry(Enchantments.SHARPNESS)
                        .orElse(null);

                if (sharpnessEntry != null) {
                    int currentSharpness = target.getEnchantments().getLevel(sharpnessEntry);

                    if (currentSharpness < 5) {
                        target.addEnchantment(sharpnessEntry, currentSharpness + 1);

                        nbt.putBoolean("EnchantedWhetstoneUsed", true);
                        target.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
                    }
                }
            }
        }

        return TypedActionResult.success(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        boolean showDetails = false;
        try {
            Class<?> screenClass = Class.forName("net.minecraft.client.gui.screen.Screen");
            var hasShiftDownMethod = screenClass.getMethod("hasShiftDown");
            showDetails = (Boolean) hasShiftDownMethod.invoke(null);
        } catch (ClassNotFoundException e) {
            Keepsakes.LOGGER.error("CLIENT: Screen class not found - this is expected on server");
        } catch (Exception e) {
            Keepsakes.LOGGER.error("CLIENT: Reflection failed: {}", e.getMessage(), e);
        }

        // ? Lore
        tooltip.add(Text.translatable("item.keepsakes.enchanted_whetstone.lore").formatted(Formatting.DARK_GRAY));

        tooltip.add(Text.translatable("item.keepsakes.enchanted_whetstone.ability").formatted(Formatting.GOLD)
                .append(Text.translatable(!showDetails ? "item.keepsakes.ability.hold_shift" : "").formatted(Formatting.DARK_GRAY)));
        if (showDetails) {
            tooltip.add(Text.translatable("item.keepsakes.enchanted_whetstone.ability_tooltip").formatted(Formatting.GRAY));
        }
    }
}