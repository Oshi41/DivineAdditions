package divineadditions.capability.knowledge;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class KnowledgeProvider implements ICapabilitySerializable<NBTBase> {
    private final IKnowledgeInfo cap;

    public KnowledgeProvider(IKnowledgeInfo cap) {

        this.cap = cap;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == IKnowledgeInfo.KnowledgeCapability;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return hasCapability(capability, facing)
                ? IKnowledgeInfo.KnowledgeCapability.cast(cap)
                : null;
    }

    @Override
    public NBTBase serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("Level", cap.getLevel());
        compound.setInteger("DefenderSummons", cap.armorDefenderSummonCount());
        return compound;
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        if (!(nbt instanceof NBTTagCompound))
            return;

        NBTTagCompound compound = (NBTTagCompound) nbt;
        cap.setLevel(compound.getInteger("Level"));
        cap.setArmorDefenderSummonCount(compound.getInteger("DefenderSummons"));
    }
}
