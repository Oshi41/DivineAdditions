package divineadditions.registry;

import divineadditions.DivineAdditions;
import divineadditions.entity.EntityCageBullet;
import divineadditions.render.stack.RenderModItemStack;
import divineadditions.render.tile.TileEntityStackHolderRenderer;
import divineadditions.tile.TileEntityStackHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
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

        registerEntityRenders();
        registerTESR();
        registerTileRenders();
    }


    private static void registerItemModel(Item item) {
        if (item == null)
            return;

        int meta = 0;
        ResourceLocation registryName = item.getRegistryName();
        ModelResourceLocation modelResourceLocation = new ModelResourceLocation(registryName, "inventory");
        ModelLoader.setCustomModelResourceLocation(item, meta, modelResourceLocation);
    }

    private static void registerEntityRenders() {
        RenderingRegistry.registerEntityRenderingHandler(EntityCageBullet.class, manager -> new RenderSnowball<>(manager, Items.NETHER_STAR, Minecraft.getMinecraft().getRenderItem()));
    }

    private static void registerTESR() {
        divineadditions.holders.Items.caged_mob.setTileEntityItemStackRenderer(new RenderModItemStack());
    }

    private static void registerTileRenders() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityStackHolder.class, new TileEntityStackHolderRenderer());
    }
}
