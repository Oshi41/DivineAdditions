package divineadditions.registry;

import divineadditions.DivineAdditions;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = DivineAdditions.MOD_ID, value = Side.CLIENT)
public class ModelRegistryEventHandler {

    @SubscribeEvent
    public static void registerItems(ModelRegistryEvent event) {
        ForgeRegistries
                .ITEMS
                .getEntries()
                .stream()
                .filter(x -> DivineAdditions.MOD_ID.equals(x.getKey().getResourceDomain()))
                .forEach(x -> registerItemModel(x.getValue()));
    }

    private static void registerItemModel(Item item) {
        if (item == null)
            return;

        int meta = 0;
        ResourceLocation registryName = item.getRegistryName();
        ModelResourceLocation modelResourceLocation = new ModelResourceLocation(registryName, "inventory");
        ModelLoader.setCustomModelResourceLocation(item, meta, modelResourceLocation);
    }
}
