package divineadditions.gui.conainter.base;

import divineadditions.utils.IItemHandlerHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

/**
 * Written by @offbeatwitch.
 * Licensed under MIT.
 */
public class ContainerItemHandler extends Container {
    protected IItemHandler handler;
    protected int inventoryEnd;

    public ContainerItemHandler(IItemHandler handler, EntityPlayer player) {
        this.handler = handler;

        drawHandlerSlots(handler);
        inventoryEnd = handler.getSlots();

        drawPlayerSlots(player, 102, 159);
    }

    public ContainerItemHandler(EntityPlayer player) {
        this(IItemHandlerHelper.fromMainHand(player), player);
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

    protected void drawPlayerSlots(EntityPlayer player, int topSlotHeight, int hotbarHeight) {
        for (int l = 0; l < 3; ++l) {
            for (int j1 = 0; j1 < 9; ++j1) {
                this.addSlotToContainer(new Slot(player.inventory, j1 + l * 9 + 9, 8 + j1 * 18, topSlotHeight + l * 18));
            }
        }

        for (int i1 = 0; i1 < 9; ++i1) {
            this.addSlotToContainer(new Slot(player.inventory, i1, 8 + i1 * 18, hotbarHeight));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    public SPacketOpenWindow toPacket(String type, String name) {
        return new SPacketOpenWindow(this.windowId, type, new TextComponentString(name), this.handler.getSlots());
    }

    public void open(EntityPlayerMP player, String type, String title) {
        player.getNextWindowId();
        this.windowId = player.currentWindowId;

        player.connection.sendPacket(this.toPacket(type, title));
        player.openContainer = this;
        this.addListener(player);
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
