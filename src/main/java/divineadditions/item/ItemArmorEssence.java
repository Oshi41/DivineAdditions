package divineadditions.item;

import divineadditions.api.IArmorEssence;
import divinerpg.api.armor.IFullSetInfo;
import divinerpg.api.armor.registry.IArmorDescription;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ItemArmorEssence extends Item implements IArmorEssence {

    public ItemArmorEssence() {

    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(durabilityName)) {
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
            tooltip.addAll(printFullSetPerks(description));
        }
    }

    /**
     * TODO request DivineRPG dev team to fix bug
     *
     * @param description
     * @return
     */
    private List<String> printFullSetPerks(IArmorDescription description) {
        IFullSetInfo item = Arrays.stream(EntityEquipmentSlot.values())
                .map(x -> description.getPossibleItems(x).stream().filter(y -> y instanceof IFullSetInfo).findFirst().orElse(null))
                .filter(Objects::nonNull)
                .map(x -> (IFullSetInfo) x)
                .findFirst().orElse(null);

        if (item != null && item.getFullSetPerks() != null) {
            return item.getFullSetPerks().getSiblings().stream().map(ITextComponent::getFormattedText).collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

}
