package divineadditions.registry;

import divineadditions.DivineAdditions;
import divineadditions.holders.Tabs;
import divineadditions.item.ItemArmorEssence;
import divineadditions.item.ItemCagedMob;
import divineadditions.item.ItemModRifle;
import divineadditions.item.rifle_core.ItemRifleMobCore;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

@Mod.EventBusSubscriber(modid = DivineAdditions.MOD_ID)
public class ItemRegistryHandler {
    @SubscribeEvent
    public static void registerItems(final RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        registerObj(registry, new ItemArmorEssence().setCreativeTab(Tabs.Main), "armor_essence");
        registerObj(registry, new Item().setCreativeTab(Tabs.Main), "rifle_entity_bullet");
        registerObj(registry, new ItemModRifle().setCreativeTab(Tabs.Main), "rifle");
        registerObj(registry, new ItemRifleMobCore().setCreativeTab(Tabs.Main), "rifle_mob_core");
        registerObj(registry, new ItemCagedMob().setCreativeTab(Tabs.Main), "caged_mob");
    }

    private static <T extends IForgeRegistryEntry<T>> void registerObj(IForgeRegistry<T> registry, T value, String name) {

        value = value.setRegistryName(new ResourceLocation(DivineAdditions.MOD_ID, name));

        if (value instanceof Block) {
            ((Block) value).setUnlocalizedName(name);
        }

        if (value instanceof Item) {
            ((Item) value).setUnlocalizedName(name);
        }

        registry.register(value);
    }
}
