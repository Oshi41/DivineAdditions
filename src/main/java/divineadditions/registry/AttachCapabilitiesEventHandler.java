package divineadditions.registry;

import divineadditions.DivineAdditions;
import divineadditions.capability.CapabilityItemProvider;
import divineadditions.tile.TileEntityStackHolder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.ItemStackHandler;

@Mod.EventBusSubscriber(modid = DivineAdditions.MOD_ID)
public class AttachCapabilitiesEventHandler {
    public static final ResourceLocation ItemHandlerId = new ResourceLocation(DivineAdditions.MOD_ID, "item_handler_cap");

    @SubscribeEvent
    public static void attachToTiles(AttachCapabilitiesEvent<TileEntity> event) {
        if (event.getObject() instanceof TileEntityStackHolder) {
            event.addCapability(ItemHandlerId, new CapabilityItemProvider(new ItemStackHandler() {
                @Override
                public int getSlotLimit(int slot) {
                    return 1;
                }
            }));
        }
    }
}
