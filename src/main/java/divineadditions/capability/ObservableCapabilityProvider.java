package divineadditions.capability;

import divineadditions.DivineAdditions;
import divineadditions.event.EntityJoinedEventHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import javax.annotation.Nonnull;
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
    private final WeakReference<EntityPlayer> reference;
    private final Function<T, IMessage> createMsg;
    private final T prevInstance;

    /**
     * previous nbt state. If changed, we'll send message to client
     */
    private NBTBase prev;

    public ObservableCapabilityProvider(Capability<T> current, T instance, EntityPlayer player, Function<T, IMessage> createMsg) {
        super(current, instance);
        this.createMsg = createMsg;
        reference = new WeakReference<>(player);

        prev = new NBTTagCompound();
        prevInstance = current.getDefaultInstance();

        if (player.getEntityWorld().isRemote) {
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

        EntityPlayer player = reference.get();
        if (player == null)
            return;

        onChange(player, prevInstance);

        prev = newValue.copy();
        current.readNBT(prevInstance, null, prev);
    }

    /**
     * Called after detecting actual changes
     *
     * @param player
     */
    protected void onChange(@Nonnull EntityPlayer player, T old) {
        if (player instanceof EntityPlayerMP) {
            IMessage message = createMsg.apply(instance);
            DivineAdditions.networkWrapper.sendTo(message, ((EntityPlayerMP) player));
        }
    }
}
