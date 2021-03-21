package divineadditions.api;

import divinerpg.api.armor.registry.IArmorDescription;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandlerModifiable;

public interface IBlankArmor {
    /**
     * Is wearing current armor set
     *
     * @param location - armor set id
     * @return
     */
    boolean isWearing(ItemStack stack, ResourceLocation location);

    /**
     * Getting inner container
     *
     * @param stack
     * @return
     */
    IItemHandlerModifiable getHandler(ItemStack stack);

    /**
     * trying to absorb armor essence
     *
     * @param essence
     * @return
     */
    default boolean tryAbsorb(ItemStack armor, ItemStack essence) {
        if (armor == null || essence == null || armor.isEmpty() || essence.isEmpty())
            return false;

        if (!(essence.getItem() instanceof IArmorEssence))
            return false;

        IArmorDescription description = ((IArmorEssence) essence.getItem()).getDescription(essence);
        if (description == null)
            return false;

        IItemHandlerModifiable handler = getHandler(armor);
        if (handler == null)
            return false;

        int emptySlot = -1;

        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stackInSlot = handler.getStackInSlot(i);
            if (stackInSlot.getItem() instanceof IArmorEssence
                    && description == ((IArmorEssence) stackInSlot.getItem()).getDescription(stackInSlot)) {
                // was already absorbed
                return false;
            }
        }

        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stackInSlot = handler.getStackInSlot(i);
            if (stackInSlot.isEmpty()
                    && handler.isItemValid(i, essence)) {
                handler.setStackInSlot(i, essence);
                return true;
            }
        }

        return false;
    }

    default boolean isWearing(ItemStack stack, IArmorDescription description) {
        if (description == null)
            return false;

        return isWearing(stack, description.getRegistryName());
    }
}
