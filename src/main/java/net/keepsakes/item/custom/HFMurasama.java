package net.keepsakes.item.custom;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.UnbreakableComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;

public class HFMurasama extends SwordItem {
    public HFMurasama(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings
                .maxCount(1)
                .component(DataComponentTypes.UNBREAKABLE, new UnbreakableComponent(true))
                .attributeModifiers(SwordItem.createAttributeModifiers(toolMaterial, 6, -2.0f))
                .fireproof()
        );
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        World world = attacker.getEntityWorld();
        if (attacker instanceof PlayerEntity user) {
            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    SoundEvents.BLOCK_GLASS_BREAK,
                    SoundCategory.PLAYERS, 2f, 1.0f);
        }
        return true;
    }
}
