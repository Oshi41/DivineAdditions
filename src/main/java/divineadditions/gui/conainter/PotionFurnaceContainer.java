package divineadditions.gui.conainter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class PotionFurnaceContainer extends Container {
    private final IInventory inventory;
    private final EntityPlayer player;
    private final int currentSlotsCount;
    private final NonNullList<Integer> fields;

    public PotionFurnaceContainer(IInventory inventory, EntityPlayer player) {
        this.inventory = inventory;
        this.player = player;

        int i = 0;

        InvWrapper wrapper = new InvWrapper(inventory);

        for (i = 0; i < 3; i++) {
            addSlotToContainer(new SlotItemHandler(wrapper, i, 17 + (44 * i), 47));
        }

        // fuel
        addSlotToContainer(new SlotItemHandler(wrapper, i++, 146, 51));

        currentSlotsCount = i;

        drawPlayerSlots(player, 85, 143, 8);

        fields = NonNullList.withSize(3, 0);
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
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return inventory.isUsableByPlayer(entityPlayer);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index < 9) {
                if (!this.mergeItemStack(itemstack1, currentSlotsCount, inventorySlots.size() - 1, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, currentSlotsCount, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void updateProgressBar(int id, int data) {
        inventory.setField(id, data);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        for (int i = 0; i < fields.size(); i++) {
            int currentFieldValue = inventory.getField(i);

            if (fields.get(i) != currentFieldValue) {
                fields.set(i, currentFieldValue);
                for (IContainerListener listener : listeners) {
                    listener.sendWindowProperty(this, i, currentFieldValue);
                }
            }
        }
    }
}
