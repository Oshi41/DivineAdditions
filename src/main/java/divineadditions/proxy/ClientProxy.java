package divineadditions.proxy;

import divineadditions.DivineAdditions;
import divineadditions.debug.LangHelper;
import divineadditions.gui.conainter.ForgeContainer;
import divineadditions.msg.ChangeRecipeMsg;
import divineadditions.msg.ParticleMessage;
import divineadditions.msg.base.CapChangedMessageBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Random;

@SideOnly(Side.CLIENT)
public class ClientProxy extends ProxyBase {
    @Override
    protected long getCurrentTick() {
        WorldClient world = Minecraft.getMinecraft().world;
        return world == null ? 0 : world.getTotalWorldTime();
    }

    @Nonnull
    @Override
    protected IThreadListener getThreadListener() {
        return Minecraft.getMinecraft();
    }

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
    protected IMessage handleClientMessages(IMessage message, NetHandlerPlayClient ctx) {

        final EntityPlayerSP player = Minecraft.getMinecraft().player;
        IThreadListener worldThread = FMLCommonHandler.instance().getWorldThread(ctx);

        if (message instanceof CapChangedMessageBase) {
            worldThread.addScheduledTask(() -> handleCapMsg(((CapChangedMessageBase) message), player));
            return null;
        }

        if (message instanceof ChangeRecipeMsg) {
            worldThread.addScheduledTask(() -> handleChangeRecipeMessage(((ChangeRecipeMsg) message), player));
            return null;
        }

        if (message instanceof ParticleMessage) {
            worldThread.addScheduledTask(() -> handleParticles(((ParticleMessage) message), player.world));
        }

        return super.handleClientMessages(message, ctx);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END
                && event.side == Side.CLIENT
                && !Minecraft.getMinecraft().isGamePaused()
                && Minecraft.getMinecraft().player != null) {
            update();
        }
    }

    private void handleParticles(ParticleMessage message, World world) {
        if (message == null || world == null)
            return;

        int count = message.getCount();
        EnumParticleTypes types = message.getTypes();
        Vec3d pos = message.getPos();
        AxisAlignedBB area = message.getArea();

        if (count <= 0 || area == null || area.hasNaN() || pos == null || types == null || world.isOutsideBuildHeight(new BlockPos(pos)))
            return;

        Random rand = world.rand;

        for (int i = 0; i < count; i++) {
            double distanceX = area.maxX - area.minX;
            double distanceY = area.maxY - area.minY;
            double distanceZ = area.maxZ - area.minZ;

            double speedX = (rand.nextFloat() * distanceX) + area.minX;
            double speedY = (rand.nextFloat() * distanceY) + area.minY;
            double speedZ = (rand.nextFloat() * distanceZ) + area.minZ;

            world.spawnParticle(
                    types,
                    pos.x,
                    pos.y,
                    pos.z,
                    speedX,
                    speedY,
                    speedZ
            );
        }
    }

    private void handleCapMsg(CapChangedMessageBase msg, EntityPlayer player) {
        ICapabilityProvider provider = msg.getFromPlayer(player);
        if (provider == null) {
            DivineAdditions.logger.warn("ClientProxy.handleCapMsg: Capability provider was not found from player");
        }

        Object capability = provider.getCapability(msg.getCap(), null);
        if (capability == null) {
            DivineAdditions.logger.warn("ClientProxy.handleCapMsg: Capability was not found");
            return;
        }

        msg.getCap().readNBT(capability, null, msg.getCompound());

    }

    private void handleChangeRecipeMessage(ChangeRecipeMsg msg, EntityPlayer player) {
        if (player.openContainer instanceof ForgeContainer) {
            ((ForgeContainer) player.openContainer).getCraftingSlot().setCurrentRecipe(msg.getRecipe());
        }
    }
}
