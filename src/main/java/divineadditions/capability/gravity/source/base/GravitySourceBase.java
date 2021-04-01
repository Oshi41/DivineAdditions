package divineadditions.capability.gravity.source.base;

import divineadditions.capability.gravity.event.AffectiveGravityEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.Objects;

public abstract class GravitySourceBase<T extends ICapabilityProvider> implements IGravitySource<T> {
    private final WeakReference<T> reference;

    protected GravitySourceBase(T owner) {
        reference = new WeakReference<>(owner);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Nullable
    @Override
    public T getOwner() {
        return reference.get();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GravitySourceBase)) return false;
        GravitySourceBase<?> that = (GravitySourceBase<?>) o;
        return Objects.equals(reference.get(), that.reference.get());
    }

    @Override
    public abstract int hashCode();
}
