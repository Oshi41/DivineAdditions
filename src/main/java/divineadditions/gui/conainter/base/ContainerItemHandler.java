package divineadditions.gui.conainter.base;

import divineadditions.utils.InventoryHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;

/**
 * Written by @offbeatwitch.
 * Licensed under MIT.
 */
public class ContainerItemHandler extends Container {
    protected IItemHandler handler;
    protected int inventoryEnd;

    @Nullable
    private IInventory possibleInventory;

    public ContainerItemHandler(IItemHandler handler, EntityPlayer player) {
        this.handler = handler;
        if (handler instanceof InvWrapper) {
            possibleInventory = ((InvWrapper) handler).getInv();
        }

        drawHandlerSlots(handler);
        inventoryEnd = handler.getSlots();

        drawPlayerSlots(player, 102, 159, 8);
    }

    public ContainerItemHandler(EntityPlayer player) {
        this(InventoryHelper.fromMainHand(player), player);
    }

    protected void drawHandlerSlots(IItemHandler handler) {
        int i = 0;

        while (i < handler.getSlots()) {
            int j = i % 9;
            int c = i / 9;

            Slot slot = new SlotItemHandler(handler, i, 8 + j * 18, 18 + c * 18);
            this.addSlotToContainer(slot);

            i++;
        }
    }

    protected void drawPlayerSlots(EntityPlayer player, int topSlotHeight, int hotbarHeight, int xStart) {
        for (int l = 0; l < 3; ++l) {
            for (int j1 = 0; j1 < 9; ++j1) {
                this.addSlotToContainer(new Slot(player.inventory, j1 + l * 9 + 9, xStart + j1 * 18, topSlotHeight + l * 18));
            }
        }

        for (int i1 = 0; i1 < 9; ++i1) {
            this.addSlotToContainer(new Slot(player.inventory, i1, xStart + i1 * 18, hotbarHeight));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return possibleInventory == null || possibleInventory.isUsableByPlayer(playerIn);
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack current = slot.getStack();
            stack = current.copy();

            if (index < inventoryEnd) {
                if (!this.mergeItemStack(current, inventoryEnd, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(current, 0, inventoryEnd, false)) {
                return ItemStack.EMPTY;
            }

            if (current.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return stack;
    }
}
