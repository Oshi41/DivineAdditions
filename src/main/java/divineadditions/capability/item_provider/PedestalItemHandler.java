package divineadditions.capability.item_provider;

import divineadditions.utils.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class PedestalItemHandler extends ItemStackHandler {
    private BlockPos pos;
    private int stackSize;

    public PedestalItemHandler() {

    }

    public PedestalItemHandler(int stackSize) {
        this.stackSize = stackSize;
    }

    @Override
    protected void onContentsChanged(int slot) {

    }

    @Override
    public int getSlotLimit(int slot) {
        return stackSize;
    }

    @Override
    protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
        if (stack.isStackable()) {
            return getSlotLimit(slot);
        }

        return super.getStackLimit(slot, stack);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = InventoryHelper.save(this);
        nbt.setInteger("StackSize", stackSize);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        stackSize = nbt.getInteger("StackSize");
        setSize(nbt.hasKey("Size", Constants.NBT.TAG_INT)
                ? nbt.getInteger("Size")
                : stacks.size());

        InventoryHelper.load(this, nbt);
        onLoad();
    }
}
