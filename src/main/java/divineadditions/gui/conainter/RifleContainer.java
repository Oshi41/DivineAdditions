package divineadditions.gui.conainter;

import divineadditions.gui.conainter.base.ContainerItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraftforge.items.SlotItemHandler;

public class RifleContainer extends ContainerItemHandler {

    public RifleContainer(EntityPlayer player) {
        super(player);
    }

    @Override
    protected Slot addSlotToContainer(Slot slotIn) {
        if (slotIn instanceof SlotItemHandler){

        }

        return super.addSlotToContainer(slotIn);
    }
}
