package divineadditions.proxy;

import divineadditions.DivineAdditions;
import divineadditions.api.IProxy;
import divineadditions.msg.base.CapChangedMessageBase;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ITickable;
import net.minecraft.util.Tuple;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

public abstract class ProxyBase implements IProxy, ITickable {
    protected final Map<Object, Tuple<Long, Runnable>> updates = new WeakHashMap<>();

    protected ProxyBase() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void scheduleTask(Runnable runnable) {
        getThreadListener().addScheduledTask(runnable);
    }

    @Override
    public void scheduleUpdate(Object key, Runnable runnable) {
        if (key == null || runnable == null)
            return;

        long tick = getCurrentTick() + 2;
        updates.put(tick, new Tuple<>(tick, runnable));
    }

    protected abstract long getCurrentTick();

    @Nonnull
    protected abstract IThreadListener getThreadListener();

    @Override
    public void update() {
        if (updates.isEmpty())
            return;

        long currentTick = getCurrentTick();

        Iterator<Map.Entry<Object, Tuple<Long, Runnable>>> iterator = updates.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Object, Tuple<Long, Runnable>> entry = iterator.next();

            if (entry.getValue().getFirst() <= currentTick) {
                scheduleTask(entry.getValue().getSecond());
                iterator.remove();
            }
        }
    }

    @Override
    public final IMessage onMessage(IMessage message, MessageContext ctx) {
        if (message == null || ctx == null)
            return null;

        switch (ctx.side) {
            case CLIENT:
                return handleClientMessages(message, ctx.getClientHandler());

            case SERVER:
                return handleServerMessages(message, ctx.getServerHandler());
        }

        return null;
    }

    @SideOnly(Side.CLIENT)
    protected IMessage handleClientMessages(IMessage message, net.minecraft.client.network.NetHandlerPlayClient ctx) {
        return null;
    }

    protected IMessage handleServerMessages(IMessage message, NetHandlerPlayServer handler) {
        if (message instanceof CapChangedMessageBase) {
            CapChangedMessageBase msg = (CapChangedMessageBase) message;
            ICapabilityProvider provider = msg.getFromPlayer(handler.player);
            if (provider == null) {
                DivineAdditions.logger.warn("ProxyBase.handleServerMessages: Capability provider was not found from player");
                return null;
            }

            Object capability = provider.getCapability(msg.getCap(), null);
            if (capability == null) {
                DivineAdditions.logger.warn("ProxyBase.handleServerMessages: Capability was not found");
                return null;
            }

            msg.write(capability);
            return msg;
        }

        return null;
    }
}
