package divineadditions.proxy;

import divineadditions.DivineAdditions;
import divineadditions.api.IProxy;
import divineadditions.debug.LangHelper;
import divineadditions.gui.conainter.ForgeContainer;
import divineadditions.msg.CapChangedMessageBase;
import divineadditions.msg.ChangeRecipeMsg;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.FMLCommonHandler;
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
    public IMessage onMessage(final IMessage message, MessageContext ctx) {
        if (message == null || ctx == null)
            return null;

        final EntityPlayerSP player = Minecraft.getMinecraft().player;
        IThreadListener worldThread = FMLCommonHandler.instance().getWorldThread(ctx.netHandler);

        if (message instanceof CapChangedMessageBase) {
            worldThread.addScheduledTask(() -> handleCapMsg(((CapChangedMessageBase) message), player));
            return null;
        }

        if (message instanceof ChangeRecipeMsg) {
            worldThread.addScheduledTask(() -> handleChangeRecipeMessage(((ChangeRecipeMsg) message), player));
            return null;
        }

        return null;
    }

    private void handleCapMsg(CapChangedMessageBase msg, EntityPlayer player) {
        ICapabilityProvider provider = msg.getFromPlayer(player);
        if (provider != null) {
            Object capability = provider.getCapability(msg.getCap(), null);
            if (capability != null) {
                msg.getCap().getStorage().readNBT(msg.getCap(), capability, null, msg.getCompound());
            } else {
                DivineAdditions.logger.warn("ClientProxy.handleCapMsg: Capability was not found");
            }
        } else {
            DivineAdditions.logger.warn("ClientProxy.handleCapMsg: Capability provider was not found from player");
        }
    }

    private void handleChangeRecipeMessage(ChangeRecipeMsg msg, EntityPlayer player) {
        if (player.openContainer instanceof ForgeContainer) {
            ((ForgeContainer) player.openContainer).getCraftingSlot().setCurrentRecipe(msg.getRecipe());
        }
    }
}
