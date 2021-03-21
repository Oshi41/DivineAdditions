package divineadditions.capability.item_provider;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.ItemStackHandler;

public class PedestalItemHandler extends ItemStackHandler {
    private BlockPos pos;
    private int stackSize;

    public PedestalItemHandler() {

    }

    public PedestalItemHandler(TileEntity entity, int stackSize) {
        pos = entity.getPos();
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
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = super.serializeNBT();
        nbt.setLong("BlockPos", pos.toLong());
        nbt.setInteger("StackSize", stackSize);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        super.deserializeNBT(nbt);
        stackSize = nbt.getInteger("StackSize");
        pos = BlockPos.fromLong(nbt.getLong("BlockPos"));
    }
}
