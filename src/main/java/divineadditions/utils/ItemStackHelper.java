package divineadditions.utils;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemStackHelper {
    public static ItemStack shrink(ItemStack stack, EntityLivingBase player, int count) {
        if (player instanceof EntityPlayer && ((EntityPlayer) player).isCreative()) {
            return stack;
        }

        stack.shrink(count);
        return stack;
    }
}
