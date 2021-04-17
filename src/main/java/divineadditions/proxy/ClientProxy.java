package divineadditions.proxy;

import divineadditions.DivineAdditions;
import divineadditions.api.IProxy;
import divineadditions.debug.LangHelper;
import divineadditions.gui.conainter.ForgeContainer;
import divineadditions.msg.ChangeRecipeMsg;
import divineadditions.msg.ParticleMessage;
import divineadditions.msg.base.CapChangedMessageBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
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
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

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

        if (message instanceof ParticleMessage) {
            worldThread.addScheduledTask(() -> handleParticles(((ParticleMessage) message), player.world));
        }

        return null;
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
