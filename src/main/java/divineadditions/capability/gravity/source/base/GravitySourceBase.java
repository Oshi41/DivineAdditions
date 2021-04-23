package divineadditions.capability.gravity.source.base;

import divineadditions.capability.base.OwnerCap;
import divineadditions.capability.gravity.event.AffectiveGravityEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public abstract class GravitySourceBase<T extends ICapabilityProvider> extends OwnerCap<T> implements IGravitySource<T> {
    protected GravitySourceBase(T owner) {
        super(owner);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void isAffected(AffectiveGravityEvent event) {
        if (canApplyTo(event.getEntity())) {
            event.addSource(this);
        }
    }

    /**
     * If owner was deleted, need to unsubscibe from forge events
     *
     * @return false if unsubscribed
     */
    protected boolean checkSubscription() {
        if (getOwner() == null) {
            MinecraftForge.EVENT_BUS.unregister(this);
            return false;
        } else {
            return true;
        }
    }
}
