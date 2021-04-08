package divineadditions.utils;

import com.google.common.collect.AbstractIterator;
import divineadditions.DivineAdditions;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class InventoryHelper {
    private static final String slotsTagName = "Slots";
    private static final String itemsTagName = "Items";

    private static NBTTagCompound save(Stream<ItemStack> stream) {
        NBTTagCompound compound = new NBTTagCompound();

        List<ItemStack> stacks = stream.collect(Collectors.toList());

        compound.setInteger(slotsTagName, stacks.size());

        NBTTagList tagList = new NBTTagList();

        for (int i = 0; i < stacks.size(); i++) {
            ItemStack stack = stacks.get(i);
            if (stack.isEmpty()) {
                DivineAdditions.logger.debug("empty stack to save");
            } else {
                DivineAdditions.logger.debug("not empty here");
            }

            tagList.appendTag(divineadditions.utils.ItemStackHelper.save(stack));
        }

        compound.setTag(itemsTagName, tagList);

        return compound;
    }

    private static void load(NBTTagCompound compound, BiConsumer<Integer, ItemStack> puInSlotFunc) {
        int slots = compound.getInteger(slotsTagName);
        NBTTagList items = compound.getTagList(itemsTagName, 10);

        for (int i = 0; i < slots; i++) {
            puInSlotFunc.accept(i, divineadditions.utils.ItemStackHelper.load(items.getCompoundTagAt(i)));
        }
    }

    private static Stream<ItemStack> asStream(Function<Integer, ItemStack> getStackInSlotFunc, final int maxSize) {
        Iterable<ItemStack> iterable = () -> new AbstractIterator<ItemStack>() {
            int slot = 0;

            @Override
            protected ItemStack computeNext() {
                return this.slot >= maxSize ? this.endOfData() : getStackInSlotFunc.apply(this.slot++);
            }
        };

        return StreamSupport.stream(iterable.spliterator(), false);
    }

    private static void clear(Function<Integer, ItemStack> getStackInSlotFunc, final int maxSize) {
        for (int i = 0; i < maxSize; i++) {
            getStackInSlotFunc.apply(i).setCount(0);
        }
    }

    private static void dropAll(World world, BlockPos pos, Function<Integer, ItemStack> getStackInSlotFunc, final int maxSize) {
        for (int i = 0; i < maxSize; i++) {
            ItemStack itemStack = getStackInSlotFunc.apply(i);
            if (!itemStack.isEmpty()) {
                Block.spawnAsEntity(world, pos, itemStack);
            }
        }
    }

    // region IItemHandler

    public static NBTTagCompound save(IItemHandler handler) {
        if (handler == null)
            return new NBTTagCompound();

        return save(asStream(handler));
    }

    public static void load(IItemHandlerModifiable handler, NBTTagCompound compound) {
        if (handler == null || compound == null || !compound.hasKey(slotsTagName) || !compound.hasKey(itemsTagName))
            return;

        load(compound, handler::setStackInSlot);
    }

    public static Stream<ItemStack> asStream(IItemHandler inv) {
        return asStream(inv::getStackInSlot, inv.getSlots());
    }

    public static void clear(IItemHandler inv) {
        clear(inv::getStackInSlot, inv.getSlots());
    }

    public static void dropAll(IItemHandler inv, World world, BlockPos pos) {
        dropAll(world, pos, inv::getStackInSlot, inv.getSlots());
    }

    // endregion

    // region IInventory

    public static Stream<ItemStack> asStream(IInventory inv) {
        return asStream(inv::getStackInSlot, inv.getSizeInventory());
    }

    public static NBTTagCompound save(IInventory inventory) {
        if (inventory == null)
            return new NBTTagCompound();

        NonNullList<ItemStack> inventoryStacks = NonNullList.from(ItemStack.EMPTY, asStream(inventory).toArray(ItemStack[]::new));
        NBTTagCompound result = ItemStackHelper.saveAllItems(new NBTTagCompound(), inventoryStacks, true);
        return result;
    }

    public static void load(IInventory inventory, NBTTagCompound compound) {
        if (inventory == null || compound == null || compound.getSize() == 0)
            return;

        NonNullList<ItemStack> stacks = NonNullList.withSize(inventory.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, stacks);

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            inventory.setInventorySlotContents(i, stacks.get(i));
        }
    }

    public static void clear(IInventory inv) {
        clear(inv::getStackInSlot, inv.getSizeInventory());
    }

    public static void dropAll(IInventory inv, World world, BlockPos pos) {
        dropAll(world, pos, inv::getStackInSlot, inv.getSizeInventory());
    }

    // endregion

    public static IItemHandler fromMainHand(EntityPlayer player) {
        return player.getHeldItemMainhand().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
    }

    public static boolean insert(Entity e, ItemStack... stacks) {
        if (e == null || stacks == null)
            return false;

        if (stacks.length == 0)
            return true;

        if (e instanceof EntityPlayer) {
            for (ItemStack stack : stacks) {
                if (!((EntityPlayer) e).inventory.addItemStackToInventory(stack)) {
                    net.minecraft.inventory.InventoryHelper.spawnItemStack(e.getEntityWorld(), e.posX, e.posY, e.posZ, stack);
                }
            }

            return true;
        }

        IItemHandler capability = e.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (capability == null) {
            for (ItemStack stack : stacks) {
                net.minecraft.inventory.InventoryHelper.spawnItemStack(e.getEntityWorld(), e.posX, e.posY, e.posZ, stack);
            }

            return true;
        }

        for (ItemStack stack : stacks) {
            boolean wasInserted = false;
            for (int i = 0; i < capability.getSlots() && !wasInserted; i++) {
                if (capability.isItemValid(i, stack)) {
                    wasInserted = !ItemStack.areItemsEqual(capability.insertItem(i, stack, false), stack);
                }
            }

            if (!wasInserted) {
                net.minecraft.inventory.InventoryHelper.spawnItemStack(e.getEntityWorld(), e.posX, e.posY, e.posZ, stack);
            }
        }

        return true;
    }
}
