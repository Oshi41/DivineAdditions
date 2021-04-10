package divineadditions.registry;

import divineadditions.DivineAdditions;
import divineadditions.api.IItemCapacity;
import divineadditions.capability.DefaultCapabilityProvider;
import divineadditions.capability.ObservableCapabilityProvider;
import divineadditions.capability.gravity.source.WorldGravitySource;
import divineadditions.capability.gravity.source.base.IGravitySource;
import divineadditions.capability.item_provider.CapabilityItemProvider;
import divineadditions.capability.item_provider.ItemStackHandlerExtended;
import divineadditions.capability.knowledge.IKnowledgeInfo;
import divineadditions.capability.knowledge.KnowledgeInfo;
import divineadditions.config.DivineAdditionsConfig;
import divineadditions.gui.inventory.RifleInventory;
import divineadditions.item.ItemRifle;
import divineadditions.msg.KnowledgeMessage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.wrapper.InvWrapper;

import java.util.Map;

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
                event.addCapability(GravityCap, new DefaultCapabilityProvider<>(IGravitySource.GravitySourceCap, new WorldGravitySource(world)));
            }
        }
    }

    @SubscribeEvent
    public static void attachToEntity(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(KnowledgeCap, new ObservableCapabilityProvider<>(
                    IKnowledgeInfo.KnowledgeCapability,
                    new KnowledgeInfo(),
                    (EntityPlayer) event.getObject(),
                    KnowledgeMessage::new));
        }
    }

    @SubscribeEvent
    public static void attachToStack(AttachCapabilitiesEvent<ItemStack> event) {
        if (event.getObject().getItem() instanceof ItemRifle) {
            Map<ResourceLocation, ICapabilityProvider> map = event.getCapabilities();
            if (map != null) {
                map.put(ItemHandlerId, new CapabilityItemProvider(new InvWrapper(new RifleInventory())));
            }
        }
    }
}
