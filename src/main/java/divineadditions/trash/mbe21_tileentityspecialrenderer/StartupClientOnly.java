package divineadditions.trash.mbe21_tileentityspecialrenderer;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;

/**
 * User: The Grey Ghost
 * Date: 24/12/2014
 *
 * The Startup classes for this example are called during startup, in the following order:
 *  preInitCommon
 *  preInitClientOnly
 *  initCommon
 *  initClientOnly
 *  postInitCommon
 *  postInitClientOnly
 *  See MinecraftByExample class for more information
 */
public class StartupClientOnly
{
  public static void preInitClientOnly()
  {
    // This is currently necessary in order to make your block render properly when it is an item (i.e. in the inventory
    //   or in your hand or thrown on the ground).
    // It must be done on client only, and must be done after the block has been created in Common.preinit().
    ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation("divineadditions:mbe21_tesr_block", "inventory");
    final int DEFAULT_ITEM_SUBTYPE = 0;
    ModelLoader.setCustomModelResourceLocation(StartupCommon.itemBlockMBE21, DEFAULT_ITEM_SUBTYPE, itemModelResourceLocation);  }

  public static void initClientOnly()
  {
    ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMBE21.class, new TileEntitySpecialRendererMBE21());
  }

  public static void postInitClientOnly()
  {
  }

}
