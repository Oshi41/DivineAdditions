package divineadditions.msg;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract class PlayerCapabilityChangedMessageBase<T> implements IMessage {
    private NBTTagCompound nbt;

    public PlayerCapabilityChangedMessageBase() {

    }

    public PlayerCapabilityChangedMessageBase(T instance) {
        write(instance);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        nbt = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, nbt);
    }

    public NBTTagCompound getNbt() {
        return nbt;
    }

    public void write(T instance) {
        nbt = (NBTTagCompound) getCap().getStorage().writeNBT(getCap(), instance, null);
    }

    public abstract Capability<T> getCap();
}
