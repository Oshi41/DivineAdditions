package divineadditions.gui.inventory;

import divineadditions.api.IRifleCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class RifleInventory extends InventoryBasic {
    private final int[] bulletsSlots = IntStream.range(0, 4).toArray();
    private final int[] catalystSlots = IntStream.range(4, 8).toArray();

    public RifleInventory() {
        super(new TextComponentTranslation("gui.rifle"), 9);
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index == 8) {
            return stack.getItem() instanceof IRifleCore;
        }

        ItemStack core = getCore();
        if (core.isEmpty())
            return false;

        IRifleCore rifleCore = (IRifleCore) core.getItem();


        if (0 <= index && index < 4) {
            // bullets
            return rifleCore.acceptableForBullets(stack, true);
        }

        if (4 <= index && index < 8) {
            // catalyst
            return rifleCore.acceptableForCatalyst(stack, true);
        }

        return false;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        IItemHandler capability = player.getHeldItemMainhand().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        return capability instanceof InvWrapper && ((InvWrapper) capability).getInv() == this;
    }

    public List<ItemStack> getBullets() {
        return findSomething(bulletsSlots);
    }

    public List<ItemStack> getCatalysts() {
        return findSomething(catalystSlots);
    }

    public ItemStack getCore() {
        return getStackInSlot(8);
    }

    private List<ItemStack> findSomething(int[] indexes) {
        List<ItemStack> result = new ArrayList<>();

        for (int i : indexes) {
            ItemStack slot = getStackInSlot(i);
            if (!slot.isEmpty()) {
                result.add(slot);
            }
        }

        return result;
    }
}
