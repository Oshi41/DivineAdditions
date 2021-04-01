package divineadditions.capability.gravity.source;

import divineadditions.capability.gravity.GravityUtils;
import divineadditions.capability.gravity.source.base.GravitySourceBase;
import net.minecraft.entity.Entity;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;

public class ChunkGravitySource extends GravitySourceBase<Chunk> {
    private final int hashCode;
    private double multiplier;

    public ChunkGravitySource(Chunk owner, double multiplier) {
        super(owner);
        this.multiplier = multiplier;
        hashCode = owner.getPos().hashCode();
    }

    @Override
    public double getMultiplier() {
        return multiplier;
    }

    @Override
    public void setMultiplier(double value) {
        multiplier = value;
    }

    @Override
    public boolean applyGravity(ICapabilityProvider provider) {
        if (provider instanceof Chunk) {
            for (ClassInheritanceMultiMap<Entity> map : ((Chunk) provider).getEntityLists()) {
                map.forEach(entity -> GravityUtils.applyGravity(entity, this));
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean canApplyTo(@Nonnull Entity e) {
        Chunk chunk = getOwner();
        if (chunk == null)
            return false;

        if (!new ChunkPos(e.getPosition()).equals(chunk.getPos())) {
            return false;
        }

        if (e.getEntityWorld() != chunk.getWorld())
            return false;

        for (ClassInheritanceMultiMap<Entity> map : chunk.getEntityLists()) {
            if (map.contains(e))
                return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END
                && checkSubscription()
                && event.world == getOwner().getWorld()) {
            applyGravity(getOwner());
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END
                && event.side == Side.CLIENT
                && checkSubscription()) {
            net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getMinecraft();

            if (!minecraft.isGamePaused() && minecraft.player != null && minecraft.world == getOwner().getWorld()) {
                applyGravity(getOwner());
            }
        }
    }
}
