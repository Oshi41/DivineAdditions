package divineadditions.capability.gravity;

import divineadditions.capability.gravity.source.base.IGravitySource;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HandleJumpEvent {
    @SubscribeEvent
    public static void handleJump(LivingFallEvent event) {
        Entity entity = event.getEntity();
        if (entity == null)
            return;

        // calculating current gravity source for entity
        AffectiveGravityEvent gravityEvent = new AffectiveGravityEvent(entity);
        MinecraftForge.EVENT_BUS.post(gravityEvent);
        // not found
        if (gravityEvent.getCurrentSources().isEmpty())
            return;

        // getting the resulting multiplier
        double totalMultiplier = gravityEvent.getCurrentSources().stream().mapToDouble(IGravitySource::getMultiplier).reduce((left, right) -> left * right).orElse(0);
        if (totalMultiplier == 0)
            return;

        event.setDistance((float) (event.getDistance() * totalMultiplier));
    }
}
