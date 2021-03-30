package divineadditions.capability.item_provider;

import divineadditions.api.IItemCapacity;
import divineadditions.utils.InventoryHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;

public class ItemStackHandlerExtended extends ItemStackHandler {
    protected WeakReference<TileEntity> entity;
    protected int stackSize;

    public ItemStackHandlerExtended() {

    }

    public ItemStackHandlerExtended(IItemCapacity tile) {
        this(tile.getStackSize(), tile.getSlotCount(), null);

        if (tile instanceof TileEntity) {
            entity = new WeakReference<>((TileEntity) tile);
        }
    }

    public ItemStackHandlerExtended(int stackSize, int slotCount, @Nullable TileEntity entity) {
        super(slotCount);
        this.stackSize = stackSize;

        if (entity != null)
            this.entity = new WeakReference<>(entity);
    }

    public static void sendUpdate(TileEntity tileEntity) {
        if (tileEntity != null && tileEntity.getWorld() != null) {
            World world = tileEntity.getWorld();
            BlockPos pos = tileEntity.getPos();
            IBlockState state = world.getBlockState(pos);

            if (state != null) {
                world.notifyBlockUpdate(pos, state, state, 3);
            }
        }
    }

    @Override
    protected void onContentsChanged(int slot) {
        if (entity != null) {
            sendUpdate(entity.get());
        }
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
