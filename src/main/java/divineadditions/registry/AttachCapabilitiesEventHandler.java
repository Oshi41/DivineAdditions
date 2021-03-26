package divineadditions.registry;

import divineadditions.DivineAdditions;
import divineadditions.api.IPedestal;
import divineadditions.capability.item_provider.CapabilityItemProvider;
import divineadditions.capability.item_provider.PedestalItemHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = DivineAdditions.MOD_ID)
public class AttachCapabilitiesEventHandler {
    public static final ResourceLocation ItemHandlerId = new ResourceLocation(DivineAdditions.MOD_ID, "item_handler_cap");


    @SubscribeEvent
    public static void attachToTiles(AttachCapabilitiesEvent<TileEntity> event) {
        final TileEntity tileEntity = event.getObject();

        if (tileEntity instanceof IPedestal) {
            event.addCapability(ItemHandlerId, new CapabilityItemProvider(new PedestalItemHandler(((IPedestal) tileEntity).getStackSize())));
        }
    }
}
