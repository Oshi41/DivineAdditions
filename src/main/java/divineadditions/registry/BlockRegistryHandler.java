package divineadditions.registry;

import divineadditions.DivineAdditions;
import divineadditions.block.BlockCatalystStand;
import divineadditions.block.BlockTimeBeacon;
import divineadditions.holders.Tabs;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = DivineAdditions.MOD_ID)
public class BlockRegistryHandler {
    private final static List<Item> blockItems = new ArrayList<>();

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void registerItems(final RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        while (!blockItems.isEmpty()) {
            registry.register(blockItems.get(0));
            blockItems.remove(0);
        }
    }

    @SubscribeEvent
    public static void registerBlocks(final RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();
        registerObj(registry, new BlockTimeBeacon().setLightLevel(1.0F).setHardness(3F).setBlockUnbreakable().setCreativeTab(Tabs.Main), "time_beacon", true);
        registerObj(registry, new BlockCatalystStand().setHardness(0.5F).setLightLevel(0.125F).setCreativeTab(Tabs.Main), "catalyst_stand", true);
    }

    private static void registerObj(IForgeRegistry<Block> registry, Block value, String name, boolean canBeItem) {
        ResourceLocation id = new ResourceLocation(DivineAdditions.MOD_ID, name);

        value.setUnlocalizedName(name)
                .setRegistryName(id);

        registry.register(value);

        if (canBeItem) {
            Item item = new ItemBlock(value)
                    .setUnlocalizedName(name)
                    .setRegistryName(id);

            blockItems.add(item);
        }
    }
}
