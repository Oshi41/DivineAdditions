package divineadditions.gui.inventory;

import divineadditions.api.ICraftingCore;
import divineadditions.api.IEntityCage;
import divineadditions.capability.item_provider.ItemStackHandlerExtended;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class InventoryForge extends ItemStackHandlerExtended {

    public InventoryForge() {
        super(5 * 5 + 2, 64);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        // cage mob slot
        if (getSlots() - 2 == slot) {
            return stack.getItem() instanceof IEntityCage;
        }

        // core slot
        if (getSlots() - 1 == slot) {
            return stack.getItem() instanceof ICraftingCore;
        }

        return super.isItemValid(slot, stack);
    }
}
