package divineadditions.holders;

import divineadditions.DivineAdditions;
import divinerpg.enums.ArmorInfo;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.util.EnumHelper;

public class ToolMaterials {
    public static final Item.ToolMaterial SoulSwordMaterial = EnumHelper.addToolMaterial(
            new ResourceLocation(DivineAdditions.MOD_ID, "soul_sword").toString(),
            0,
            1200,
            1,
            10,
            14);
    public static ItemArmor.ArmorMaterial BlankMaterial = createArmorMaterial(new ResourceLocation(DivineAdditions.MOD_ID, "blank"),
            15,
            new int[]{2, 5, 6, 2},
            9,
            SoundEvents.ITEM_ARMOR_EQUIP_IRON,
            0.0F);

    public static ArmorInfo BlankArmorInfo = new ArmorInfo(new TextComponentTranslation("divineadditions.blank_armor_info"));


    private static ItemArmor.ArmorMaterial createArmorMaterial(ResourceLocation id, int durability, int[] reductionAmounts, int enchantability, SoundEvent soundOnEquip, float toughness) {
        return EnumHelper.addArmorMaterial(id.toString(), id.toString(), durability, reductionAmounts, enchantability, soundOnEquip, toughness);
    }
}
