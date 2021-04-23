package divineadditions.capability.gravity.source.base;

import divineadditions.capability.base.IOwnerCap;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public interface IGravitySource<T extends ICapabilityProvider> extends IOwnerCap<T> {
    @CapabilityInject(IGravitySource.class)
    Capability<IGravitySource> GravitySourceCap = null;

    /**
     * Gets current multiplier
     *
     * @return
     */
    double getMultiplier();

    /**
     * Settings current multiplier (mostly from NBT)
     *
     * @param value
     */
    void setMultiplier(double value);

    /**
     * Should call every tick
     *
     * @param provider
     * @return false if current source can't be listen on EVENT_BUS
     */
    boolean applyGravity(ICapabilityProvider provider);

    /**
     * Currently enable to entity
     * Fast implementation!!!
     *
     * @param e entity
     * @return
     */
    boolean canApplyTo(Entity e);

    @Override
    int hashCode();
}
