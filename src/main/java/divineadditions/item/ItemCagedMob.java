package divineadditions.item;

import divineadditions.api.IEntityCage;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemCagedMob extends Item implements IEntityCage {
    public ItemCagedMob() {
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        ResourceLocation id = getContainingEntityId(stack.getTagCompound());
        if (id != null) {
            String translationKey = String.format("entity.%s.name", EntityList.getTranslationName(id));
            TextComponentTranslation translation = new TextComponentTranslation(translationKey);
            tooltip.add(translation.getFormattedText());
        }

    }
}
