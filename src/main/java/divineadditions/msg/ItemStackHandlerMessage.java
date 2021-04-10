package divineadditions.msg;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class ItemStackHandlerMessage extends CapChangedMessageBase<IItemHandler> {

    public ItemStackHandlerMessage() {
    }

    public ItemStackHandlerMessage(IItemHandler handler) {
        super(handler);
    }

    @Override
    public ICapabilityProvider getFromPlayer(@Nonnull EntityPlayer player) {
        return player.getHeldItemMainhand();
    }

    @Override
    public Capability<IItemHandler> getCap() {
        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }
}
