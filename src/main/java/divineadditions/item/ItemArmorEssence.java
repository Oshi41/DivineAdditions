package divineadditions.item;

import divineadditions.api.IArmorEssence;
import divinerpg.api.DivineAPI;
import divinerpg.api.armor.registry.IArmorDescription;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class ItemArmorEssence extends Item implements IArmorEssence {
    private static final String armorDescriptionName = "ArmorId";
    private static final String durabilityName = "Durability";

    public ItemArmorEssence() {

    }

    @Nullable
    @Override
    public IArmorDescription getDescription(ItemStack stack) {
        if (checkStack(stack)) {
            String string = stack.getTagCompound().getString(armorDescriptionName);
            ResourceLocation id = new ResourceLocation(string);
            IArmorDescription armorDescription = DivineAPI.getArmorDescriptionRegistry().getValue(id);
            return armorDescription;
        }

        return null;
    }

    @Override
    public boolean absorb(ItemStack essence, Map<EntityEquipmentSlot, ItemStack> items, IArmorDescription description) {
        if (checkStack(essence) && items != null && description != null) {
            NBTTagCompound compound = essence.getTagCompound();

            if (!"".equals(compound.getString(armorDescriptionName))) {
                compound.setString(armorDescriptionName, description.getRegistryName().toString());
                int durability = items
                        .values()
                        .stream()
                        .filter(x -> !x.isEmpty())
                        .mapToInt(x -> x.getMaxDamage() - x.getItemDamage())
                        .sum();

                compound.setInteger(durabilityName, durability);
                return true;
            }
        }

        return false;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        if (checkStack(stack)) {
            return stack.getTagCompound().getInteger(durabilityName);
        }
        return super.getMaxDamage(stack);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return getDescription(stack) != null;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        IArmorDescription description = getDescription(stack);
        if (description != null) {
            tooltip.add(description.getFullSetPerks().getFormattedText());
        }
    }

    private boolean checkStack(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof IArmorEssence))
            return false;

        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(armorDescriptionName)) {
            compound.setString(armorDescriptionName, "");
        }

        if (!compound.hasKey(durabilityName)) {
            compound.setInteger(durabilityName, 0);
        }

        return true;
    }
}
