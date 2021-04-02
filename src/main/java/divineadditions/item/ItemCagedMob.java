package divineadditions.item;

import divineadditions.api.IEntityCage;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.List;

public class ItemCagedMob extends Item implements IEntityCage {
    public ItemCagedMob() {
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        NBTTagCompound cageNbt = stack.getSubCompound(IEntityCage.cagedTagName);
        if (cageNbt.getSize() == 0)
            return;

        ResourceLocation id = new ResourceLocation(cageNbt.getString("id"));
        String translationKey = String.format("entity.%s.name", EntityList.getTranslationName(id));
        ITextComponent translation = new TextComponentTranslation(translationKey);

        if (cageNbt.hasKey("DeathTime")) {
            translation.getStyle().setColor(TextFormatting.RED);
        }

        tooltip.add(translation.getFormattedText());

        float health = cageNbt.getFloat("Health");
        if (health > 0) {
            String format = new DecimalFormat("#0.0").format(health);
            translation = new TextComponentTranslation("divineadditions.tooltip.dna_capacity", format);
            tooltip.add(translation.getFormattedText());
        }

    }
}
