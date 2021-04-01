package divineadditions.capability;

import divineadditions.DivineAdditions;
import divineadditions.event.EntityJoinedEventHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.function.Function;

/**
 * Check the difference in capability when serializing it. Sends message to client only
 * See {@link divineadditions.event.EntityJoinedEventHandler} to check for initial capability syns
 *
 * @param <T>
 */
public class ObservableCapabilityProvider<T> extends DefaultCapabilityProvider<T> {
    private final WeakReference<EntityPlayerMP> reference;
    private final Function<T, IMessage> createMsg;

    /**
     * previous nbt state. If changed, we'll send message to client
     */
    private NBTBase prev = null;

    public ObservableCapabilityProvider(Capability<T> current, T instance, EntityPlayer player, Function<T, IMessage> createMsg) {
        super(current, instance);
        this.createMsg = createMsg;
        reference = player instanceof EntityPlayerMP
                ? new WeakReference<>(((EntityPlayerMP) player))
                : null;

        if (reference == null) {
            // registering current capability as syns only on client
            EntityJoinedEventHandler.register(current, createMsg);
        }
    }

    @Override
    public NBTBase serializeNBT() {
        NBTBase nbt = super.serializeNBT();
        detectChanges(nbt);
        return nbt;
    }

    private void detectChanges(NBTBase newValue) {
        if (reference == null || reference.get() == null)
            return;

        if (Objects.equals(newValue, prev))
            return;

        prev = newValue.copy();
        EntityPlayerMP entityPlayer = reference.get();
        IMessage msg = createMsg.apply(instance);
        DivineAdditions.networkWrapper.sendTo(msg, entityPlayer);
    }
}
