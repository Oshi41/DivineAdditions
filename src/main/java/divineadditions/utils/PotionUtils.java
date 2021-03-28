package divineadditions.utils;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public class PotionUtils {

    @SideOnly(Side.CLIENT)
    public static void addToolTipNew(List<PotionEffect> list, List<String> lores, float durationFactor) {
        for (PotionEffect potioneffect : list) {

            TextComponentTranslation component = new TextComponentTranslation(potioneffect.getEffectName());
            Potion potion = potioneffect.getPotion();

            if (potioneffect.getAmplifier() > 0) {
                component.appendText(" ").appendSibling(new TextComponentTranslation("potion.potency." + potioneffect.getAmplifier()));
            }

            if (potioneffect.getDuration() > 20) {
                component.appendText(" (" + Potion.getPotionDurationString(potioneffect, durationFactor) + ")");
            }

            component.getStyle().setColor(potion.isBadEffect() ? TextFormatting.RED : TextFormatting.BLUE);

            lores.add(component.getFormattedText());
        }
    }

    public static NBTTagCompound writeEffects(@Nullable NBTTagCompound nbttagcompound, Collection<PotionEffect> effects) {
        if (nbttagcompound == null) {
            nbttagcompound = new NBTTagCompound();
        }

        NBTTagList nbttaglist = nbttagcompound.getTagList("CustomPotionEffects", 9);

        for (PotionEffect potioneffect : effects) {
            nbttaglist.appendTag(potioneffect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
        }

        nbttagcompound.setTag("CustomPotionEffects", nbttaglist);
        return nbttagcompound;
    }
}
