package divineadditions.capability.gravity.source;

import divineadditions.capability.gravity.GravityUtils;
import divineadditions.capability.gravity.source.base.GravitySourceBase;
import divineadditions.config.DivineAdditionsConfig;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;
import java.util.Objects;

public class WorldGravitySource extends GravitySourceBase<World> {
    private final String name;

    public WorldGravitySource(World owner) {
        super(owner);

        name = owner.provider.getDimensionType().getName();
    }

    @Override
    public double getMultiplier() {
        return DivineAdditionsConfig.gravity.getOrDefault(name, 1.);
    }

    @Override
    public void setMultiplier(double value) {
        // value is based on config, do not need to change it
    }

    @Override
    public boolean applyGravity(ICapabilityProvider provider) {
        if (provider instanceof World) {
            List<Entity> loadedEntityList = ((World) provider).loadedEntityList;

            for (int i = 0; i < loadedEntityList.size(); i++) {
                Entity entity = loadedEntityList.get(i);
                GravityUtils.applyGravity(entity, this);
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean canApplyTo(Entity e) {
        return e != null && getOwner() == e.getEntityWorld();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @SubscribeEvent
    protected void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END
                && checkSubscription()
                && event.world == getOwner()) {
            applyGravity(getOwner());
        }
    }

    @SubscribeEvent
    protected void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END
                && event.side == Side.CLIENT
                && checkSubscription()) {
            net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getMinecraft();

            if (!minecraft.isGamePaused() && minecraft.player != null && minecraft.world == getOwner()) {
                applyGravity(getOwner());
            }
        }
    }
}
