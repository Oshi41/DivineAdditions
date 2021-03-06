package divineadditions.registry;

import divineadditions.DivineAdditions;
import divineadditions.holders.Tabs;
import divineadditions.holders.ToolMaterials;
import divineadditions.item.*;
import divineadditions.item.rifle_core.ItemRifleCoreBullet;
import divineadditions.item.rifle_core.ItemRifleMobCore;
import divineadditions.item.sword.ItemCustomSword;
import divineadditions.item.sword.SwordProperties;
import divinerpg.DivineRPG;
import divinerpg.registry.DivineRPGTabs;
import divinerpg.registry.MaterialRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.ResourceLocation;
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
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void registerItems(final RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        registerObj(registry, new ItemArmorEssence().setCreativeTab(Tabs.Main), "armor_essence");
        registerObj(registry, new Item().setCreativeTab(Tabs.Main), "rifle_bullet");
        registerObj(registry, new ItemRifle().setCreativeTab(Tabs.Main), "rifle");
        registerObj(registry, new ItemRifleMobCore().setCreativeTab(Tabs.Main), "rifle_mob_core");
        registerObj(registry, new ItemCagedMob(), "caged_mob");
        registerObj(registry, new Item().setCreativeTab(Tabs.Main), "soul_powder");

        registerObj(registry, new ItemMod().immortal().setCreativeTab(Tabs.Main), "empty_infinite_gem");
        registerObj(registry, new ItemMod().immortal().setCreativeTab(Tabs.Main), "power_gem");
        registerObj(registry, new ItemMod()
                .withInfo("divineadditions.tooltip.not_using_in_craft")
                .repair(Items.IRON_INGOT)
                .setMaxDamage(64)
                .setMaxStackSize(1)
                .setCreativeTab(Tabs.Main), "rifle_template");

        registerObj(registry, new ItemCustomSword(ToolMaterials.SoulSwordMaterial,
                new SwordProperties().setSoulPerKills(60)
        ).setCreativeTab(Tabs.Main), "soul_sword");

        registerObj(registry, new ItemKnowledgeBook(1).setCreativeTab(Tabs.Main), "book_of_knowledge_1");
        registerObj(registry, new Item().setCreativeTab(Tabs.Main), "time_drop");
        registerObj(registry, new ItemRifleCoreBullet().setCreativeTab(Tabs.Main), "rifle_bullet_core");
        registerObj(registry, new ItemDefenderStand(false).setCreativeTab(Tabs.Main), "defender_stand");
        registerObj(registry, new ItemDefenderStand(true).setCreativeTab(Tabs.Main), "defender_stand_activation");
        registerArmorSet(registry, Tabs.Main, "blank", (slot) -> new ItemBlankArmor(ToolMaterials.BlankMaterial, slot, ToolMaterials.BlankArmorInfo));

        registerObj(registry, new ItemInstantRangedWeapon(32, ToolMaterials.SerenadeDamageMaterial).setCreativeTab(Tabs.Main), "serenade_of_damage");
        registerObj(registry, new ItemPotionBucket().setCreativeTab(CreativeTabs.BREWING), "potion_bucket");
    }

    /**
     * override section
     *
     * @param event
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerOverrides(final RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        registerObj(registry, new ItemCustomSword(MaterialRegistry.EDEN_BLADE, new SwordProperties()).setCreativeTab(DivineRPGTabs.MELEE_WEAPONS), new ResourceLocation(DivineRPG.MODID, "eden_blade"));
        registerObj(registry, new ItemCustomSword(MaterialRegistry.WILDWOOD_BLADE, new SwordProperties()).setCreativeTab(DivineRPGTabs.MELEE_WEAPONS), new ResourceLocation(DivineRPG.MODID, "wildwood_blade"));
        registerObj(registry, new ItemCustomSword(MaterialRegistry.APALACHIA_BLADE, new SwordProperties()).setCreativeTab(DivineRPGTabs.MELEE_WEAPONS), new ResourceLocation(DivineRPG.MODID, "apalachia_blade"));
        registerObj(registry, new ItemCustomSword(MaterialRegistry.SKYTHERN_BLADE, new SwordProperties()).setCreativeTab(DivineRPGTabs.MELEE_WEAPONS), new ResourceLocation(DivineRPG.MODID, "skythern_blade"));
        registerObj(registry, new ItemCustomSword(MaterialRegistry.MORTUM_BLADE, new SwordProperties()).setCreativeTab(DivineRPGTabs.MELEE_WEAPONS), new ResourceLocation(DivineRPG.MODID, "mortum_blade"));
        registerObj(registry, new ItemCustomSword(MaterialRegistry.HALITE_BLADE, new SwordProperties()).setCreativeTab(DivineRPGTabs.MELEE_WEAPONS), new ResourceLocation(DivineRPG.MODID, "halite_blade"));
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
            itemArmor.setUnlocalizedName(prefix)
                    .setRegistryName(prefix)
                    .setCreativeTab(tabs);
            registry.register(itemArmor);
        }
    }

    private static void registerObj(IForgeRegistry<Item> registry, Item value, String name) {
        registerObj(registry, value, new ResourceLocation(DivineAdditions.MOD_ID, name));
    }

    private static void registerObj(IForgeRegistry<Item> registry, Item value, ResourceLocation id) {
        value.setUnlocalizedName(id.getResourcePath()).setRegistryName(id);
        registry.register(value);
    }
}
