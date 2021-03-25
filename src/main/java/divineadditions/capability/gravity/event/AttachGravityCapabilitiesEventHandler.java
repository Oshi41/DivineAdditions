package divineadditions.capability.gravity.event;

import divineadditions.DivineAdditions;
import divineadditions.capability.gravity.GravitySourceCapabilityProvider;
import divineadditions.capability.gravity.source.WorldGravitySource;
import divineadditions.config.DivineAdditionsConfig;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = DivineAdditions.MOD_ID)
public class AttachGravityCapabilitiesEventHandler {
    public static final ResourceLocation GravityCap = new ResourceLocation(DivineAdditions.MOD_ID, "gravity_source_cap");

    @SubscribeEvent
    public static void attachToWorld(AttachCapabilitiesEvent<World> event) {
        final World world = event.getObject();
        if (world != null) {
            Double multiplier = DivineAdditionsConfig.gravity.get(world.provider.getDimensionType().getName());
            if (multiplier != null && multiplier != 1) {
                event.addCapability(GravityCap, new GravitySourceCapabilityProvider(new WorldGravitySource(world)));
            }
        }
    }
}
