package divineadditions.api;

import divinerpg.api.armor.registry.IArmorDescription;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Map;

public interface IArmorEssence {

    /**
     * Current armor set description
     *
     * @param stack essence stack
     * @return
     */
    @Nullable
    IArmorDescription getDescription(ItemStack stack);

    /**
     * Tying to absorb armor set
     *
     * @param essence     - essence stack
     * @param items       - armor set to absorb
     * @param description - armor set description
     * @return
     */
    boolean absorb(ItemStack essence, Map<EntityEquipmentSlot, ItemStack> items, IArmorDescription description);
}
