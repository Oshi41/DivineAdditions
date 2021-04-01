package divineadditions.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DefaultCapabilityProvider<T> implements ICapabilitySerializable<NBTBase> {
    protected final Capability<T> current;
    protected final T instance;

    public DefaultCapabilityProvider(Capability<T> current, T instance) {
        this.current = current;
        this.instance = instance;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == current;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return hasCapability(capability, facing)
                ? current.cast(instance)
                : null;
    }

    @Override
    public NBTBase serializeNBT() {
        return current.getStorage().writeNBT(current, instance, null);
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        current.readNBT(instance, null, nbt);
    }
}
