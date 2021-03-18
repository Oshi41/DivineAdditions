package divineadditions.registry;

import divineadditions.DivineAdditions;
import divineadditions.holders.Tabs;
import divineadditions.item.ItemArmorEssence;
import divineadditions.item.ItemCagedMob;
import divineadditions.item.ItemModRifle;
import divineadditions.item.rifle_core.ItemRifleMobCore;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = DivineAdditions.MOD_ID)
public class ItemRegistryHandler {

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void registerItems(final RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        registerObj(registry, new ItemArmorEssence().setCreativeTab(Tabs.Main), "armor_essence");
        registerObj(registry, new Item().setCreativeTab(Tabs.Main), "rifle_bullet");
        registerObj(registry, new ItemModRifle().setCreativeTab(Tabs.Main), "rifle");
        registerObj(registry, new ItemRifleMobCore().setCreativeTab(Tabs.Main), "rifle_mob_core");
        registerObj(registry, new ItemCagedMob(), "caged_mob");
    }

    private static void registerObj(IForgeRegistry<Item> registry, Item value, String name) {
        value.setUnlocalizedName(name)
                .setRegistryName(new ResourceLocation(DivineAdditions.MOD_ID, name));

        registry.register(value);
    }
}
