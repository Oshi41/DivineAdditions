package divineadditions.registry;

import divineadditions.DivineAdditions;
import divineadditions.api.IItemCapacity;
import divineadditions.capability.gravity.GravitySourceCapabilityProvider;
import divineadditions.capability.gravity.source.WorldGravitySource;
import divineadditions.capability.item_provider.CapabilityItemProvider;
import divineadditions.capability.item_provider.ItemStackHandlerExtended;
import divineadditions.capability.knowledge.KnowledgeInfo;
import divineadditions.capability.knowledge.KnowledgeProvider;
import divineadditions.config.DivineAdditionsConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = DivineAdditions.MOD_ID)
public class AttachCapabilitiesEventHandler {
    public static final ResourceLocation ItemHandlerId = new ResourceLocation(DivineAdditions.MOD_ID, "item_handler_cap");
    public static final ResourceLocation GravityCap = new ResourceLocation(DivineAdditions.MOD_ID, "gravity_source_cap");
    public static final ResourceLocation KnowledgeCap = new ResourceLocation(DivineAdditions.MOD_ID, "knowledge_cap");

    @SubscribeEvent
    public static void attachToTiles(AttachCapabilitiesEvent<TileEntity> event) {
        final TileEntity tileEntity = event.getObject();

        if (tileEntity instanceof IItemCapacity) {
            event.addCapability(ItemHandlerId, new CapabilityItemProvider(new ItemStackHandlerExtended(((IItemCapacity) tileEntity))));
        }
    }

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

    @SubscribeEvent
    public static void attachToEntity(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(KnowledgeCap, new KnowledgeProvider(new KnowledgeInfo()));
        }
    }
}
