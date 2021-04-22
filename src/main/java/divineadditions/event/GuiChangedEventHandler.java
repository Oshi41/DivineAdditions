package divineadditions.event;

import divineadditions.DivineAdditions;
import divineadditions.api.IContainerSync;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

// @Mod.EventBusSubscriber(modid = DivineAdditions.MOD_ID)
public class GuiChangedEventHandler<T extends Container> {

    /**
     * Sending syns message to client
     *
     * @param event
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void handleGuiOpened(PlayerContainerEvent.Open event) {
        if (event.getContainer() instanceof IContainerSync && event.getEntityPlayer() instanceof EntityPlayerMP) {
            DivineAdditions.networkWrapper.sendTo(((IContainerSync) event.getContainer()).createMessage(), ((EntityPlayerMP) event.getEntityPlayer()));
        }
    }
}
