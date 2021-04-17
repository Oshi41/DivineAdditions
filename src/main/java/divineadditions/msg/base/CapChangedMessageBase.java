package divineadditions.msg.base;

import divineadditions.DivineAdditions;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import javax.annotation.Nonnull;

/**
 * Base class for capability changed message
 *
 * @param <T>
 */
public abstract class CapChangedMessageBase<T> implements IMessage {
    private NBTTagCompound compound = new NBTTagCompound();

    public CapChangedMessageBase() {

    }

    public CapChangedMessageBase(@Nonnull T instance) {
        write(instance);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        compound = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, compound);
    }

    @Nonnull
    public NBTTagCompound getCompound() {
        return compound;
    }

    public CapChangedMessageBase<T> write(@Nonnull T instance) {
        Capability<T> capability = getCap();

        NBTBase base = capability.getStorage().writeNBT(capability, instance, null);

        if (base instanceof NBTTagCompound) {
            compound = (NBTTagCompound) base;
        } else {
            DivineAdditions.logger.warn("Current instance is not NBTTagCompound");
        }

        return this;
    }

    /**
     * Returns current capability provider from player
     *
     * @param player - current player entity
     * @return
     */
    public abstract ICapabilityProvider getFromPlayer(@Nonnull EntityPlayer player);

    /**
     * Returns current capability
     *
     * @return
     */
    public abstract Capability<T> getCap();
}
