package divineadditions.capability.gravity.event;

import divineadditions.capability.gravity.source.base.IGravitySource;
import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;

import java.util.HashSet;
import java.util.Set;

public class AffectiveGravityEvent extends EntityEvent {
    private Set<IGravitySource> currentSources = new HashSet<>();

    public AffectiveGravityEvent(Entity entity) {
        super(entity);
    }

    public Set<IGravitySource> getCurrentSources() {
        return currentSources;
    }

    public void addSource(IGravitySource source) {
        currentSources.add(source);
    }
}
