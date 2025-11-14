package net.keepsakes.item.base;

import io.wispforest.accessories.api.AccessoryItem;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class GenericAccessoryItem extends AccessoryItem {
    private final int maxStates;
    private final boolean defaultLocked;

    public GenericAccessoryItem(Settings settings, int maxStates, boolean defaultLocked) {
        super(settings.maxCount(1));
        this.maxStates = Math.max(1, maxStates);
        this.defaultLocked = defaultLocked;
    }

    public int getAbilityState(ItemStack stack) {
        NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (customData != null) {
            NbtCompound nbt = customData.copyNbt();
            return nbt.getInt("AbilityState");
        }
        return 0; // Default
    }

    public void setAbilityState(ItemStack stack, int state) {
        NbtComponent customData = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = customData.copyNbt();

        nbt.putInt("AbilityState", Math.max(0, Math.min(state, maxStates -1)));
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    public void cycleAbilityState(ItemStack stack) {
        int currentState = getAbilityState(stack);
        int nextState = (currentState + 1) % maxStates;
        setAbilityState(stack, nextState);
    }

    public boolean isAbilityLocked(ItemStack stack) {
        NbtComponent customData = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        if (customData != null) {
            NbtCompound nbt = customData.copyNbt();
            return nbt.getBoolean("AbilityLocked");
        }
        return defaultLocked;
    }

    public void setAbilityLocked(ItemStack stack, boolean locked) {
        NbtComponent customData = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = customData.copyNbt();

        nbt.putBoolean("AbilityLocked", locked);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    public void toggleAbilityLocked(ItemStack stack) {
        setAbilityLocked(stack, !isAbilityLocked(stack));
    }

    public int getMaxStates() {
        return maxStates;
    }

    protected void onStateChanged(ItemStack stack, PlayerEntity player, int oldState, int newState) {
        // Called when the item switches its state
        // Override in inherited class
    }

    protected void onStateChangeBlocked(ItemStack stack, PlayerEntity player, int currentState) {
        // Called when the item is prevented from switching its state
        // Override in inherited class
    }

    protected boolean canCycleState(ItemStack stack, PlayerEntity player) {
        return !isAbilityLocked(stack);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        int oldState = getAbilityState(stack);

        if (!canCycleState(stack, user)) {
            onStateChangeBlocked(stack, user, oldState);
            return TypedActionResult.fail(stack);
        }

        cycleAbilityState(stack);
        int newState = getAbilityState(stack);

        onStateChanged(stack, user, oldState, newState);

        return TypedActionResult.success(stack, world.isClient);
    }

    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        if (player.getWorld().isClient()) return false;
        int oldState = getAbilityState(stack);

        if (!canCycleState(stack, player)) {
            onStateChangeBlocked(stack, player, oldState);
            return true;
        }

        cycleAbilityState(stack);
        int newState = getAbilityState(stack);

        onStateChanged(stack, player, oldState, newState);
        return true;
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference reference) {
        if (stack.get(DataComponentTypes.CUSTOM_DATA) == null) {
            NbtCompound nbt = new NbtCompound();
            nbt.putInt("AbilityState", 0);
            nbt.putBoolean("AbilityLocked", defaultLocked);
            stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
        }
    }
}
