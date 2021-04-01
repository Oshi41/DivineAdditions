package divineadditions.proxy;

import divineadditions.DivineAdditions;
import divineadditions.api.IProxy;
import divineadditions.debug.LangHelper;
import divineadditions.msg.PlayerCapabilityChangedMessageBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy implements IProxy {

    @Override
    public boolean isDedicatedServer() {
        return false;
    }

    @Override
    public void pre() {
        OBJLoader.INSTANCE.addDomain(DivineAdditions.MOD_ID);
    }

    @Override
    public void init() {
    }

    @Override
    public void post() {
        new LangHelper(DivineAdditions.MOD_ID).fill();
    }

    @Override
    public void scheduleTask(Runnable runnable) {
        Minecraft.getMinecraft().addScheduledTask(runnable);
    }

    @Override
    public IMessage onMessage(IMessage message, MessageContext ctx) {
        if (message == null || ctx == null)
            return null;

        EntityPlayerSP player = Minecraft.getMinecraft().player;

        if (message instanceof PlayerCapabilityChangedMessageBase) {
            return handleCapChangedMessage(((PlayerCapabilityChangedMessageBase) message), player);
        }

        return null;
    }

    private IMessage handleCapChangedMessage(PlayerCapabilityChangedMessageBase msg, EntityPlayer player) {
        Object capability = player.getCapability(msg.getCap(), null);
        if (capability != null) {
            msg.getCap().getStorage().readNBT(msg.getCap(), capability, null, msg.getNbt());
        }

        return null;
    }
}
