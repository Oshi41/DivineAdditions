package divineadditions.registry;

import divineadditions.DivineAdditions;
import divineadditions.capability.CapabilityItemProvider;
import divineadditions.holders.Items;
import divineadditions.gui.inventory.RifleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.wrapper.InvWrapper;

@Mod.EventBusSubscriber(modid = DivineAdditions.MOD_ID)
public class AttachCapabilitiesEventHandler {
    private static final ResourceLocation itemHandlerCapability = new ResourceLocation(DivineAdditions.MOD_ID, "ITEM_HANDLER_CAPABILITY");

    @SubscribeEvent
    public static void injectCapability(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack itemStack = event.getObject();

        if (itemStack.getItem() == Items.rifle) {
            event.addCapability(itemHandlerCapability, new CapabilityItemProvider(new InvWrapper(new RifleInventory())));
        }
    }
}
