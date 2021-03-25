package divineadditions.api;

import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;

/**
 * Simple event to detect if we can instant kill the entity.
 * if event was canceled means we cannot
 */
public class InfiniteAttackEvent extends EntityEvent {
    public InfiniteAttackEvent(Entity entity) {
        super(entity);
    }
}
