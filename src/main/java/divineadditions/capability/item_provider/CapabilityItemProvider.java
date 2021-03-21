package divineadditions.capability.item_provider;

import divineadditions.utils.InventoryHelper;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilityItemProvider implements ICapabilitySerializable<NBTBase> {
    private final IItemHandlerModifiable instance;

    public CapabilityItemProvider(IItemHandlerModifiable instance) {
        this.instance = instance;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
                ? CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.instance)
                : null;
    }

    @Override
    public NBTBase serializeNBT() {
        return InventoryHelper.save(instance);
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        if (nbt instanceof NBTTagCompound) {
            InventoryHelper.load(instance, (NBTTagCompound) nbt);
        }
    }
}
