package divineadditions.gui.conainter;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

public class PedestalItemHandler extends ItemStackHandler {
    private BlockPos pos;
    private int stackSize;

    private World world;

    public PedestalItemHandler() {

    }

    public PedestalItemHandler(TileEntity entity, int stackSize) {
        world = entity.getWorld();
        pos = entity.getPos();
        this.stackSize = stackSize;
    }

    @Override
    protected void onContentsChanged(int slot) {
        if (world != null && pos != null) {
            IBlockState blockState = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, blockState, blockState, 3);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return stackSize;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = super.serializeNBT();
        nbt.setInteger("StackSize", stackSize);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        super.deserializeNBT(nbt);
        stackSize = nbt.getInteger("StackSize");
    }
}
