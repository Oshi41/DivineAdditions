package divineadditions.registry;

import divineadditions.DivineAdditions;
import divineadditions.holders.Tabs;
import divineadditions.item.ItemArmorEssence;
import divineadditions.item.ItemBlankArmor;
import divineadditions.item.ItemCagedMob;
import divineadditions.item.ItemModRifle;
import divineadditions.item.rifle_core.ItemRifleMobCore;
import divinerpg.enums.ArmorInfo;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = DivineAdditions.MOD_ID)
public class ItemRegistryHandler {
    // region fields
    public static ItemArmor.ArmorMaterial BlankMaterial = createArmorMaterial(new ResourceLocation(DivineAdditions.MOD_ID, "blank"), 15, new int[]{2, 5, 6, 2}, 9, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F);
    public static ArmorInfo BlankArmorInfo = new ArmorInfo(new TextComponentTranslation("divineadditions.blank_armor_info"));

    // endregion

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void registerItems(final RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        registerObj(registry, new ItemArmorEssence().setCreativeTab(Tabs.Main), "armor_essence");
        registerObj(registry, new Item().setCreativeTab(Tabs.Main), "rifle_bullet");
        registerObj(registry, new ItemModRifle().setCreativeTab(Tabs.Main), "rifle");
        registerObj(registry, new ItemRifleMobCore().setCreativeTab(Tabs.Main), "rifle_mob_core");
        registerObj(registry, new ItemCagedMob(), "caged_mob");

        registerArmorSet(registry, Tabs.Main, "blank", (slot) -> new ItemBlankArmor(BlankMaterial, slot, BlankArmorInfo));
    }

    private static void registerArmorSet(IForgeRegistry<Item> registry,
                                         CreativeTabs tabs,
                                         String resourcePath,
                                         Function<EntityEquipmentSlot, ItemArmor> createArmor) {
        List<ItemArmor> armorList = Arrays.asList(
                createArmor.apply(EntityEquipmentSlot.HEAD),
                createArmor.apply(EntityEquipmentSlot.CHEST),
                createArmor.apply(EntityEquipmentSlot.LEGS),
                createArmor.apply(EntityEquipmentSlot.FEET)
        );

        ResourceLocation resourceLocation = new ResourceLocation(DivineAdditions.MOD_ID, resourcePath);

        for (ItemArmor itemArmor : armorList) {
            String prefix = resourceLocation.toString() + "_" + itemArmor.armorType.getName();
            itemArmor.setUnlocalizedName(prefix).setRegistryName(prefix).setCreativeTab(tabs);
            registry.register(itemArmor);
        }
    }

    private static void registerObj(IForgeRegistry<Item> registry, Item value, String name) {
        value.setUnlocalizedName(name)
                .setRegistryName(new ResourceLocation(DivineAdditions.MOD_ID, name));

        registry.register(value);
    }

    private static ItemArmor.ArmorMaterial createArmorMaterial(ResourceLocation id, int durability, int[] reductionAmounts, int enchantability, SoundEvent soundOnEquip, float toughness) {
        return EnumHelper.addArmorMaterial(id.toString(), id.toString(), durability, reductionAmounts, enchantability, soundOnEquip, toughness);
    }
}
