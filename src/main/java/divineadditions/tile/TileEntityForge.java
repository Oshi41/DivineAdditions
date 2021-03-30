package divineadditions.tile;

import divineadditions.api.IEntityCage;
import divineadditions.api.IForgeInventory;
import divineadditions.capability.item_provider.ItemStackHandlerExtended;
import divineadditions.holders.Fluids;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.TileFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class TileEntityForge extends TileFluidHandler implements IForgeInventory {
    protected final Map<EntityPlayer, Integer> currentPlayer = new HashMap<>();

    protected int size;
    protected int catalystRadius;
    protected ItemStackHandlerExtended itemHandler;


    public TileEntityForge() {
        size = 5;
        catalystRadius = 1;

        tank = new FluidTank(Fluid.BUCKET_VOLUME * 100) {
            @Override
            public boolean canFillFluidType(FluidStack fluid) {
                return fluid != null && fluid.getFluid() == Fluids.DNA;
            }
        };

        itemHandler = new ItemStackHandlerExtended(64, 1, null) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                // caged mobs
                if (slot == 0) {
                    return stack.getItem() instanceof IEntityCage;
                }

                return super.isItemValid(slot, stack);
            }

            @Override
            protected void onContentsChanged(int slot) {
                // caged mobs
                ItemStack stackInSlot = getStackInSlot(0);
                TileEntityForge.this.consumeDNA(stackInSlot);
                sendUpdate(TileEntityForge.this);
            }
        };
    }

    private boolean consumeDNA(ItemStack stackInSlot) {
        if (stackInSlot == null || !(stackInSlot.getItem() instanceof IEntityCage))
            return false;

        int health = ((int) stackInSlot.getSubCompound(IEntityCage.cagedTagName).getFloat("Health"));
        if (tank.getCapacity() - tank.getFluidAmount() < health) {
            return false;
        }

        stackInSlot.shrink(1);
        return tank.fill(new FluidStack(Fluids.DNA, health), true) > 0;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        NBTTagCompound nbt = super.writeToNBT(tag);
        nbt.setTag("Inv", itemHandler.serializeNBT());
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        itemHandler.deserializeNBT(tag.getCompoundTag("Inv"));
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemHandler);
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public int getHeight() {
        return size;
    }

    @Override
    public int getWidth() {
        return size;
    }

    @Override
    public IFluidTank getCurrentDna() {
        return tank;
    }

    @Override
    public IItemHandler getCurrentHandler() {
        return itemHandler;
    }

    @Override
    public EntityPlayer getCraftingPlayer() {
        return currentPlayer.keySet().stream().findFirst().orElse(null);
    }

    @Override
    public Map<TileEntity, IItemHandler> findCatalystStands() {
        Map<TileEntity, IItemHandler> map = StreamSupport.stream(BlockPos.getAllInBoxMutable(
                getPos().add(-catalystRadius, -catalystRadius, -catalystRadius),
                getPos().add(catalystRadius, catalystRadius, catalystRadius)).spliterator(), false)
                .map(x -> world.getTileEntity(x))
                .filter(x -> x != this && x instanceof TileEntityCatalystStand)
                .collect(Collectors.toMap(x -> x, x -> x.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)));
        return map;
    }

    @Override
    public void openInventory(EntityPlayer player) {
        Integer integer = currentPlayer.get(player);
        integer = integer == null ? 1 : integer + 1;
        currentPlayer.put(player, integer);
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        Integer integer = currentPlayer.get(player);
        if (integer != null) {
            integer--;
            if (integer < 1) {
                currentPlayer.remove(player);
            } else {
                currentPlayer.put(player, integer);
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return getCraftingPlayer() == null || getCraftingPlayer() == playerIn;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), getBlockMetadata(), getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }
}
