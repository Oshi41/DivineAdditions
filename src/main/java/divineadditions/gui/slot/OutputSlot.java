package divineadditions.gui.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.SlotFurnaceOutput;
import net.minecraft.item.ItemStack;

public class OutputSlot extends SlotFurnaceOutput {
    private final EntityPlayer player;
    private final Container eventListener;

    public OutputSlot(EntityPlayer player, Container eventListener, IInventory inventoryIn, int slotIndex, int xPosition, int yPosition) {
        super(player, inventoryIn, slotIndex, xPosition, yPosition);
        this.player = player;
        this.eventListener = eventListener;
    }

    @Override
    protected void onCrafting(ItemStack stack) {
        stack.onCrafting(this.player.world, this.player, this.removeCount);
        eventListener.onCraftMatrixChanged(inventory);
        // todo make output
    }
}
