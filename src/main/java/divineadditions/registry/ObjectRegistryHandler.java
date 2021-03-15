package divineadditions.registry;

import divineadditions.DivineAdditions;
import divineadditions.holders.Tabs;
import divineadditions.item.ItemArmorEssence;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber
public class ObjectRegistryHandler {
    private static Set<Block> toRegister = new HashSet<>();

    @SubscribeEvent
    public static void registerItems(final RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        registerObj(registry, new ItemArmorEssence().setCreativeTab(Tabs.Main), "armor_essence");
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
