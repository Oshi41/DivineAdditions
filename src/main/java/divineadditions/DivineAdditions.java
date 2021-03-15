package divineadditions;

import divineadditions.api.IProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
        modid = DivineAdditions.MOD_ID,
        name = DivineAdditions.MOD_NAME,
        version = DivineAdditions.MOD_VERSION,
        dependencies = "required:divinerpg"
)
public class DivineAdditions {
    // you also need to update the modid and version in two other places as well:
    //  build.gradle file (the version, group, and archivesBaseName parameters)
    //  resources/mcmod.info (the name, description, and version parameters)
    public static final String MOD_ID = "divineadditions";
    public static final String MOD_VERSION = "0.1";
    public static final String MOD_NAME = "Divine Additions";

    public static final String GUIFACTORY = "divineadditions.mbe70_configuration.MBEGuiFactory"; //delete if MBE70 not present

    // The instance of your mod that Forge uses.  Optional.
    @Mod.Instance(DivineAdditions.MOD_ID)
    public static DivineAdditions instance;

    // Says where the client and server 'proxy' code is loaded.
    @SidedProxy(clientSide = "divineadditions.proxy.ClientProxy", serverSide = "divineadditions.proxy.ServerProxy")
    public static IProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.pre();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.post();
    }
}
