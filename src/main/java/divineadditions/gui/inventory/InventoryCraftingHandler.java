package divineadditions.gui.inventory;

import divineadditions.DivineAdditions;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

public class InventoryCraftingHandler extends InventoryCrafting {
    private final IItemHandlerModifiable handler;
    private final int width;
    private final int height;
    private final int start;
    private Container eventHandler;

    public InventoryCraftingHandler(Container eventHandlerIn, IItemHandlerModifiable handler, int width, int height) {
        this(eventHandlerIn, handler, width, height, 0);
    }

    public InventoryCraftingHandler(Container eventHandlerIn, IItemHandlerModifiable handler, int width, int height, int start) {
        super(eventHandlerIn, 0, 0);
        eventHandler = eventHandlerIn;
        this.handler = handler;
        this.width = width;
        this.height = height;
        this.start = start;

        // checking range
        if (!checkIndex(width * height + start - 1) || !checkIndex(start)) {
            DivineAdditions.logger.warn("InventoryCraftingHandler: wrong index mapping!");
        }
    }

    @Override
    public int getSizeInventory() {
        return width * height;
    }

    @Nonnull
    @Override
    public ItemStack removeStackFromSlot(int index) {
        return decrStackSize(index, Integer.MAX_VALUE);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        index += start;
        if (!checkIndex(index)) {
            return;
        }

        handler.setStackInSlot(index, stack);
        eventHandler.onCraftMatrixChanged(this);
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int index) {
        index += start;
        if (!checkIndex(index)) {
            return ItemStack.EMPTY;
        }

        return handler.getStackInSlot(index);
    }

    @Nonnull
    @Override
    public ItemStack getStackInRowAndColumn(int row, int column) {
        return row >= 0 && row < this.width
                && column >= 0 && column <= this.height
                ? this.getStackInSlot(row + column * this.width + start)
                : ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack decrStackSize(int index, int count) {
        index += start;
        if (!checkIndex(index)) {
            return ItemStack.EMPTY;
        }

        ItemStack result = handler.extractItem(index, count, false);
        if (!result.isEmpty()) {
            eventHandler.onCraftMatrixChanged(this);
        }

        return result;
    }


    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void fillStackedContents(RecipeItemHelper helper) {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                ItemStack stack = getStackInRowAndColumn(row, col);
                helper.accountStack(stack);
            }
        }

        forceUpdate();
    }

    @Override
    public boolean isEmpty() {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (!getStackInRowAndColumn(row, col).isEmpty()) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void clear() {
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                ItemStack stack = getStackInRowAndColumn(row, col);

                if (!stack.isEmpty()) {
                    stack.shrink(stack.getCount());
                }
            }
        }

        forceUpdate();
    }

    private void forceUpdate() {
        // force update

        handler.setStackInSlot(start, handler.getStackInSlot(start));
        markDirty();
    }

    private boolean checkIndex(int index) {
        return start <= index && index < handler.getSlots();
    }
}
