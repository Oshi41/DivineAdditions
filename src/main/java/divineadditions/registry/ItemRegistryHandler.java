package divineadditions.registry;

import divineadditions.DivineAdditions;
import divineadditions.config.DivineAdditionsConfig;
import divineadditions.holders.Tabs;
import divineadditions.item.ItemArmorEssence;
import divineadditions.item.ItemEntityBullet;
import divineadditions.item.ItemModRifle;
import javafx.scene.control.Tab;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(modid = DivineAdditions.MOD_ID)
public class ItemRegistryHandler {
    @SubscribeEvent
    public static void registerItems(final RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        registerObj(registry, new ItemArmorEssence().setCreativeTab(Tabs.Main), "armor_essence");
        registerObj(registry, new ItemEntityBullet(DivineAdditionsConfig.rifleSettings.rifleCatalyst, DivineAdditionsConfig.rifleSettings.rifleCatalystCount)
                .setCreativeTab(Tabs.Main), "rifle_entity_bullet");
        registerObj(registry, new ItemModRifle().setCreativeTab(Tabs.Main), "rifle");
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
