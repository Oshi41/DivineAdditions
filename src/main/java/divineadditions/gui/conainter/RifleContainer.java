package divineadditions.gui.conainter;

import divineadditions.gui.conainter.base.ContainerItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class RifleContainer extends ContainerItemHandler {

    public RifleContainer(EntityPlayer player) {
        super(player);
    }

    @Override
    protected void drawHandlerSlots(IItemHandler handler) {

        for (int i = 0; i < 4; i++) {
            int j = i / 9;
            int c = i % 9;
            Slot slot = new SlotItemHandler(handler, i, 8 + j * 18, 8 + c * 18);
            this.addSlotToContainer(slot);
        }

        int index = 4;

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                Slot slot = new SlotItemHandler(handler, index++, 89 + i * 18, 27 + j * 18);
                addSlotToContainer(slot);
            }
        }

        addSlotToContainer(new SlotItemHandler(handler, index++, 143, 36));
    }

    @Override
    protected void drawPlayerSlots(EntityPlayer player, int topSlotHeight, int hotbarHeight) {
        super.drawPlayerSlots(player, 85, 140);
    }
}
