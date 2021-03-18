package divineadditions.utils;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class InventoryHelper {
    private static final String slotsTagName = "Slots";
    private static final String itemsTagName = "Items";

    public static NBTTagCompound save(IItemHandlerModifiable handler) {
        NBTTagCompound compound = new NBTTagCompound();

        if (handler == null)
            return compound;

        compound.setInteger(slotsTagName, handler.getSlots());

        NBTTagList tagList = new NBTTagList();

        for (int i = 0; i < handler.getSlots(); i++) {
            tagList.appendTag(handler.getStackInSlot(i).serializeNBT());
        }


        compound.setTag(itemsTagName, tagList);

        return compound;
    }

    public static void load(IItemHandlerModifiable handler, NBTTagCompound compound) {
        if (handler == null || compound == null || !compound.hasKey(slotsTagName) || !compound.hasKey(itemsTagName))
            return;

        int slots = compound.getInteger(slotsTagName);

        int maxSlot = Math.min(slots, handler.getSlots());

        NBTTagList items = compound.getTagList(itemsTagName, 10);

        for (int i = 0; i < maxSlot; i++) {
            ItemStack itemStack = new ItemStack(items.getCompoundTagAt(i));
            handler.setStackInSlot(i, itemStack);
        }
    }

    public static IItemHandler fromMainHand(EntityPlayer player) {
        return player.getHeldItemMainhand().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
    }

    public static void dropAll(IItemHandler handler, World world, BlockPos pos) {
        if (handler == null || world == null || pos == null)
            return;

        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack slot = handler.getStackInSlot(0).copy();
            if (!slot.isEmpty()) {
                Block.spawnAsEntity(world, pos, slot);
            }
        }
    }

    public static void clear(IItemHandler handler) {
        if (handler == null)
            return;

        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack itemStack = handler.getStackInSlot(0);
            if (!itemStack.isEmpty()) {
                itemStack.setCount(0);
            }
        }
    }
}
