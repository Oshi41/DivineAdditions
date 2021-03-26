package divineadditions.capability.item_provider;

import divineadditions.utils.InventoryHelper;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilityItemProvider implements ICapabilitySerializable<NBTBase> {
    private final IItemHandler instance;

    public CapabilityItemProvider(IItemHandler instance) {
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
        if (instance instanceof INBTSerializable) {
            return ((INBTSerializable) instance).serializeNBT();
        }

        if (instance instanceof InvWrapper) {
            return InventoryHelper.save(((InvWrapper) instance).getInv());
        }

        return InventoryHelper.save(instance);
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        if (instance instanceof INBTSerializable) {
            ((INBTSerializable) instance).deserializeNBT(nbt);
            return;
        }

        if (instance instanceof InvWrapper && nbt instanceof NBTTagCompound) {
            InventoryHelper.load(((InvWrapper) instance).getInv(), ((NBTTagCompound) nbt));
            return;
        }

        if (nbt instanceof NBTTagCompound) {
            InventoryHelper.load(((IItemHandlerModifiable) instance), ((NBTTagCompound) nbt));
            return;
        }
    }
}
