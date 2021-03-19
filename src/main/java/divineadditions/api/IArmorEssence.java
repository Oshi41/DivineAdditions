package divineadditions.api;

import divinerpg.api.DivineAPI;
import divinerpg.api.armor.registry.IArmorDescription;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Map;

public interface IArmorEssence {
    String armorDescriptionName = "ArmorId";
    String durabilityName = "Durability";

    /**
     * Current armor set description
     *
     * @param stack essence stack
     * @return
     */
    @Nullable
    default IArmorDescription getDescription(ItemStack stack) {
        if (stack == null || stack.getTagCompound() == null || !(stack.getItem() instanceof IArmorEssence) || stack.isEmpty())
            return null;

        String string = stack.getTagCompound().getString(armorDescriptionName);
        if (string.isEmpty())
            return null;

        ResourceLocation id = new ResourceLocation(string);
        IArmorDescription result = DivineAPI.getArmorDescriptionRegistry().getValue(id);

        return result;
    }

    /**
     * Tying to absorb armor set
     *
     * @param essence     - essence stack
     * @param items       - armor set to absorb
     * @param description - armor set description
     * @return
     */
    default boolean absorb(ItemStack essence, Map<EntityEquipmentSlot, ItemStack> items, IArmorDescription description) {
        if (essence == null || items == null || description == null || items.isEmpty())
            return false;

        if (essence.isEmpty() || !(essence.getItem() instanceof IArmorEssence))
            return false;

        if (!essence.hasTagCompound())
            essence.setTagCompound(new NBTTagCompound());

        NBTTagCompound compound = essence.getTagCompound();

        if (!compound.getString(armorDescriptionName).isEmpty())
            return false;

        compound.setString(armorDescriptionName, description.getRegistryName().toString());

        int durability = items
                .values()
                .stream()
                .filter(x -> !x.isEmpty())
                .mapToInt(x -> x.getMaxDamage() - x.getItemDamage())
                .sum();

        compound.setInteger(durabilityName, durability);
        essence.setItemDamage(0);
        return true;
    }
}
