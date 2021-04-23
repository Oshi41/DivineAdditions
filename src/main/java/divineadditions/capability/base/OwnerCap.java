package divineadditions.capability.base;

import divineadditions.utils.Property;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class OwnerCap<T extends ICapabilityProvider> implements IOwnerCap<T>, Consumer<Property> {
    private final int hash;
    private Map<String, Property> props = new HashMap<>();
    private WeakReference<T> owner;

    protected OwnerCap(T owner) {
        this.owner = new WeakReference<>(owner);
        hash = owner == null ? 0 : owner.hashCode();
    }

    @Nullable
    @Override
    public T getOwner() {
        return owner.get();
    }

    protected Property getOrCreate(String name, Supplier<Property> createFunc) {
        return props.computeIfAbsent(name, s -> createFunc.get().listen(this));
    }

    @Override
    public void accept(Property property) {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OwnerCap)) return false;
        OwnerCap<?> ownerCap = (OwnerCap<?>) o;
        return owner.equals(ownerCap.owner);
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
