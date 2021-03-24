package divineadditions.capability.gravity;

import divineadditions.capability.gravity.source.base.IGravitySource;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GravitySourceCapabilityProvider implements ICapabilitySerializable<NBTBase> {
    private IGravitySource instance;

    public GravitySourceCapabilityProvider(IGravitySource instance) {
        this.instance = instance;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == IGravitySource.GravitySourceCap;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return hasCapability(capability, facing)
                ? IGravitySource.GravitySourceCap.cast(instance)
                : null;
    }

    @Override
    public NBTBase serializeNBT() {
        return new NBTTagDouble(instance.getMultiplier());
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        if (nbt instanceof NBTTagDouble) {
            instance.setMultiplier(((NBTTagDouble) nbt).getDouble());
        }
    }
}
