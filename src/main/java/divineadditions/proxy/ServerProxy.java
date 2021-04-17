package divineadditions.proxy;

import divineadditions.DivineAdditions;
import divineadditions.api.IProxy;
import divineadditions.msg.base.CapChangedMessageBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.server.FMLServerHandler;

public class ServerProxy implements IProxy {
    @Override
    public boolean isDedicatedServer() {
        return true;
    }

    @Override
    public void pre() {

    }

    @Override
    public void init() {

    }

    @Override
    public void post() {

    }

    @Override
    public void scheduleTask(Runnable runnable) {
        FMLServerHandler.instance().getServer().addScheduledTask(runnable);
    }

    @Override
    public IMessage onMessage(IMessage message, MessageContext ctx) {
        if (message == null || ctx == null)
            return null;

        EntityPlayerMP player = ctx.getServerHandler().player;

        if (message instanceof CapChangedMessageBase) {
            return handleCapMsg(((CapChangedMessageBase) message), player);
        }

        return null;
    }

    private IMessage handleCapMsg(CapChangedMessageBase msg, EntityPlayerMP playerMP) {
        ICapabilityProvider provider = msg.getFromPlayer(playerMP);
        if (provider != null) {
            Object capability = provider.getCapability(msg.getCap(), null);
            if (capability != null) {
                msg.write(capability);
                return msg;
            } else {
                DivineAdditions.logger.warn("ServerProxy.handleCapMsg: Capability was not found");
            }
        } else {
            DivineAdditions.logger.warn("ServerProxy.handleCapMsg: Capability provider was not found from player");
        }

        return null;
    }
}
