package divineadditions.capability.base;

import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

public interface IOwnerCap<T extends ICapabilityProvider> {

    @Nullable
    T getOwner();
}
