package divineadditions.gui.inventory;

import divineadditions.api.IItemEntityBullet;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import openmods.utils.InventoryUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public class RifleInventory extends InventoryBasic {
    private final int[] bulletsSlots = IntStream.range(0, 4).toArray();
    private final int[] catalystSlots = IntStream.range(4, 8).toArray();

    public RifleInventory() {
        super(new TextComponentTranslation("gui.rifle"), 8);
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (0 <= index && index < 4) {
            // bullets
            return stack.getItem() instanceof IItemEntityBullet;
        }

        if (4 <= index && index < 8) {
            // catalyst

            Optional<ItemStack> any = StreamSupport.stream(InventoryUtils.asIterable(this).spliterator(), false)
                    .limit(4)
                    .filter(x -> !x.isEmpty())
                    .map(x -> ((IItemEntityBullet) x.getItem()).getCatalyst())
                    .filter(x -> ItemStack.areItemsEqual(x, stack) && ItemStack.areItemsEqual(x, stack))
                    .findAny();

            return any.isPresent();
        }

        return false;
    }

    public List<ItemStack> getBullets() {
        return findSomething(bulletsSlots);
    }

    public List<ItemStack> getCatalysts() {
        return findSomething(catalystSlots);
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
