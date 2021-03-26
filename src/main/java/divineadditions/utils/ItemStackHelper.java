package divineadditions.utils;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemStackHelper {
    public static ItemStack shrink(ItemStack stack, EntityLivingBase player, int count) {
        if (player instanceof EntityPlayer && ((EntityPlayer) player).isCreative()) {
            return stack;
        }

        stack.shrink(count);
        return stack;
    }

    public static NBTTagCompound save(ItemStack stack) {
        NBTTagCompound nbt = stack.serializeNBT();
        nbt.setInteger("Count", stack.getCount());
        return nbt;
    }

    public static ItemStack load(NBTTagCompound compound) {
        ItemStack stack = new ItemStack(compound);
        stack.setCount(compound.getInteger("Count"));
        return stack;
    }
}
