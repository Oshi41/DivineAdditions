package divineadditions.proxy;

import divineadditions.api.IProxy;
import divineadditions.msg.PlayerCapabilityChangedMessageBase;
import net.minecraft.entity.player.EntityPlayerMP;
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

        if (message instanceof PlayerCapabilityChangedMessageBase) {
            return handleCapMsg(((PlayerCapabilityChangedMessageBase) message), player);
        }

        return null;
    }

    private IMessage handleCapMsg(PlayerCapabilityChangedMessageBase msg, EntityPlayerMP playerMP) {
        if (msg == null || playerMP == null)
            return null;

        Object capability = playerMP.getCapability(msg.getCap(), null);
        if (capability == null)
            return null;

        msg.write(capability);
        return msg;
    }
}
