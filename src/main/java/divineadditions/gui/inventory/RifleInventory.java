package divineadditions.gui.inventory;

import divineadditions.api.IItemEntityBullet;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;

public class RifleInventory extends InventoryBasic {
    public RifleInventory() {
        super(new TextComponentTranslation("gui.rifle"), 2);
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        switch (index) {
            // bullets
            case 0:
                return stack.getItem() instanceof IItemEntityBullet;

            case 1:
                Item item = getStackInSlot(0).getItem();
                if (!(item instanceof IItemEntityBullet))
                    return false;

                ItemStack catalyst = ((IItemEntityBullet) item).getCatalyst();

                return ItemStack.areItemStackShareTagsEqual(catalyst, stack)
                        && ItemStack.areItemsEqual(catalyst, stack);


            default:
                return false;
        }
    }
}
