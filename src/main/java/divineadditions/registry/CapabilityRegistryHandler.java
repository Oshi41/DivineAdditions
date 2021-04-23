package divineadditions.registry;

import divineadditions.capability.gravity.source.ItemGravitySource;
import divineadditions.capability.gravity.source.base.IGravitySource;
import divineadditions.capability.knowledge.IKnowledgeInfo;
import divineadditions.capability.knowledge.KnowledgeInfo;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

public class CapabilityRegistryHandler {

    /**
     * !!!Important!!!
     * All storages must return NBTTagCompound instance
     */
    public static void register() {
        CapabilityManager.INSTANCE.register(IKnowledgeInfo.class, new Capability.IStorage<IKnowledgeInfo>() {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<IKnowledgeInfo> capability, IKnowledgeInfo instance, EnumFacing side) {
                NBTTagCompound compound = new NBTTagCompound();
                compound.setInteger("Level", instance.level().get());
                compound.setInteger("DefenderSummon", instance.defender().get());
                return compound;
            }

            @Override
            public void readNBT(Capability<IKnowledgeInfo> capability, IKnowledgeInfo instance, EnumFacing side, NBTBase nbt) {
                if (!(nbt instanceof NBTTagCompound))
                    return;

                NBTTagCompound compound = (NBTTagCompound) nbt;

                instance.level().set(compound.getInteger("Level"));
                instance.defender().set(compound.getInteger("DefenderSummon"));
            }
        }, KnowledgeInfo::new);

        CapabilityManager.INSTANCE.register(IGravitySource.class, new Capability.IStorage<IGravitySource>() {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<IGravitySource> capability, IGravitySource instance, EnumFacing side) {
                NBTTagCompound compound = new NBTTagCompound();
                compound.setDouble("Multiplier", instance.getMultiplier());
                return compound;
            }

            @Override
            public void readNBT(Capability<IGravitySource> capability, IGravitySource instance, EnumFacing side, NBTBase nbt) {
                if (nbt instanceof NBTTagCompound) {
                    instance.setMultiplier(((NBTTagCompound) nbt).getDouble("Multiplier"));
                }
            }
        }, () -> new ItemGravitySource(ItemStack.EMPTY, 1));
    }
}
