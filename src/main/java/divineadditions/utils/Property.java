package divineadditions.utils;

import java.util.WeakHashMap;
import java.util.function.Consumer;

public class Property<T> {
    private final WeakHashMap<Consumer<Property<T>>, Boolean> listeners;
    T value;

    public Property(T val) {
        value = val;
        listeners = new WeakHashMap<>();
    }

    public Property listen(Consumer<Property<T>> consumer) {
        listeners.put(consumer, true);
        return this;
    }

    public T get() {
        return value;
    }

    public void set(T newValue) {
        if (value == newValue)
            return;

        value = newValue;
        listeners.keySet().forEach(x -> x.accept(this));
    }
}
