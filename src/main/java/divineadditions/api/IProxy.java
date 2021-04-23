package divineadditions.api;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;

public interface IProxy extends IMessageHandler<IMessage, IMessage> {
    /**
     * is this a dedicated server?
     *
     * @return true if this is a dedicated server, false otherwise
     */
    boolean isDedicatedServer();

    default void pre() {

    }

    default void init() {

    }

    default void post() {

    }

    void scheduleTask(Runnable runnable);

    void scheduleUpdate(Object key, Runnable runnable);
}


