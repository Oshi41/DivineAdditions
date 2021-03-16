package divineadditions.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class IItemHandlerHelper {
    public static NBTTagCompound save(IItemHandler handler) {
        NBTTagCompound compound = new NBTTagCompound();

        if (handler == null)
            return compound;

        compound.setInteger("Slots", handler.getSlots());

        NBTTagList tagList = new NBTTagList();

        for (int i = 0; i < handler.getSlots(); i++) {
            tagList.appendTag(handler.getStackInSlot(i).serializeNBT());
        }

        compound.setTag("Items", tagList);

        return compound;
    }

    public static void load(IItemHandler handler, NBTTagCompound compound) {
        if (handler == null || compound == null || !compound.hasKey("Slots") || !compound.hasKey("Items"))
            return;

        int slots = compound.getInteger("Slots");

        int maxSlot = Math.min(slots, handler.getSlots());

        NBTTagList items = compound.getTagList("Items", 10);

        for (int i = 0; i < maxSlot; i++) {
            handler.insertItem(i, new ItemStack(items.getCompoundTagAt(i)), false);
        }
    }

    public static IItemHandler fromMainHand(EntityPlayer player) {
        return player.getHeldItemMainhand().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
    }
}
