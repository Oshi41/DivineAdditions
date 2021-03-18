package divineadditions;

import divineadditions.api.IProxy;
import divineadditions.gui.GuiHandler;
import divineadditions.registry.TilesRegistryHandler;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod(
        modid = DivineAdditions.MOD_ID,
        name = DivineAdditions.MOD_NAME,
        version = DivineAdditions.MOD_VERSION,
        dependencies = "required:divinerpg@[1.7,);required:openmods"
)
public class DivineAdditions {
    // you also need to update the modid and version in two other places as well:
    //  build.gradle file (the version, group, and archivesBaseName parameters)
    //  resources/mcmod.info (the name, description, and version parameters)
    public static final String MOD_ID = "divineadditions";
    public static final String MOD_VERSION = "0.1";
    public static final String MOD_NAME = "Divine Additions";

    // The instance of your mod that Forge uses.  Optional.
    @Mod.Instance(DivineAdditions.MOD_ID)
    public static DivineAdditions instance;

    // Says where the client and server 'proxy' code is loaded.
    @SidedProxy(clientSide = "divineadditions.proxy.ClientProxy", serverSide = "divineadditions.proxy.ServerProxy")
    public static IProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.pre();

        ConfigManager.sync(DivineAdditions.MOD_ID, Config.Type.INSTANCE);
        NetworkRegistry.INSTANCE.registerGuiHandler(DivineAdditions.instance, new GuiHandler());
        TilesRegistryHandler.register();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.post();
    }

    @SubscribeEvent
    public static void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(MOD_ID)) {
            ConfigManager.sync(MOD_ID, Config.Type.INSTANCE);
        }
    }
}
