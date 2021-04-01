package divineadditions.event;

import divineadditions.DivineAdditions;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = DivineAdditions.MOD_ID)
public class EntityJoinedEventHandler {
    /**
     * Capabilities we need to sync.
     * Filling from {@link divineadditions.capability.ObservableCapabilityProvider}
     */
    private static Map<Capability, Function<Object, IMessage>> capabilities = new HashMap<>();

    @SideOnly(Side.CLIENT)
    public static <T> void register(Capability<T> cap, Function<T, IMessage> messageFunc) {
        if (!capabilities.containsKey(cap))
            capabilities.put(cap, o -> messageFunc.apply((T) o));
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote) {
            final Entity entity = event.getEntity();
            capabilities.forEach((capability, func) -> gatherCapFromServer(capability, entity, func));
        }
    }

    /**
     * Requesting capability from server
     *
     * @param cap         - entity capability
     * @param entity      - current client entity
     * @param messageFunc - function returning message to server
     * @param <T>         - type of capability
     */
    @SideOnly(Side.CLIENT)
    private static <T> void gatherCapFromServer(Capability<T> cap, Entity entity, Function<T, IMessage> messageFunc) {
        if (cap == null || entity == null || messageFunc == null) {
            DivineAdditions.logger.warn("EntityJoinedEventHandler:gatherCapFromServer incorrect params");
            return;
        }

        T entityCapability = entity.getCapability(cap, null);
        if (entityCapability == null)
            return;

        DivineAdditions.networkWrapper.sendToServer(messageFunc.apply(entityCapability));
    }
}
