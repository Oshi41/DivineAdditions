package divineadditions.trash;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * ClientProxy is used to set up the mod and start it running on normal minecraft.  It contains all the code that should run on the
 *   client side only.
 *   For more background information see here http://greyminecraftcoder.blogspot.com/2013/11/how-forge-starts-up-your-code.html
 */
public class ClientOnlyProxy extends CommonProxy
{

  /**
   * Run before anything else. Read your config, create blocks, items, etc, and register them with the GameRegistry
   */
  public void preInit()
  {
    super.preInit();
    divineadditions.trash.mbe70_configuration.StartupClientOnly.preInitClientOnly();
    
    divineadditions.trash.mbe01_block_simple.StartupClientOnly.preInitClientOnly();
    divineadditions.trash.mbe02_block_partial.StartupClientOnly.preInitClientOnly();
    divineadditions.trash.mbe03_block_variants.StartupClientOnly.preInitClientOnly();
    divineadditions.trash.mbe04_block_dynamic_block_model1.StartupClientOnly.preInitClientOnly();
    divineadditions.trash.mbe05_block_dynamic_block_model2.StartupClientOnly.preInitClientOnly();
    divineadditions.trash.mbe06_redstone.StartupClientOnly.preInitClientOnly();
    divineadditions.trash.mbe08_creative_tab.StartupClientOnly.preInitClientOnly();
    divineadditions.trash.mbe10_item_simple.StartupClientOnly.preInitClientOnly();
    divineadditions.trash.mbe11_item_variants.StartupClientOnly.preInitClientOnly();
    divineadditions.trash.mbe12_item_nbt_animate.StartupClientOnly.preInitClientOnly();
    divineadditions.trash.mbe13_item_tools.StartupClientOnly.preInitClientOnly();
    divineadditions.trash.mbe15_item_dynamic_item_model.StartupClientOnly.preInitClientOnly();
    divineadditions.trash.mbe20_tileentity_data.StartupClientOnly.preInitClientOnly();
    divineadditions.trash.mbe21_tileentityspecialrenderer.StartupClientOnly.preInitClientOnly();
    divineadditions.trash.mbe30_inventory_basic.StartupClientOnly.preInitClientOnly();
    divineadditions.trash.mbe31_inventory_furnace.StartupClientOnly.preInitClientOnly();
    divineadditions.trash.mbe35_recipes.StartupClientOnly.preInitClientOnly();
    divineadditions.trash.mbe40_hud_overlay.StartupClientOnly.preInitClientOnly();
    divineadditions.trash.mbe50_particle.StartupClientOnly.preInitClientOnly();
    divineadditions.trash.mbe60_network_messages.StartupClientOnly.preInitClientOnly();
    divineadditions.trash.mbe75_testing_framework.StartupClientOnly.preInitClientOnly();
    divineadditions.trash.testingarea.StartupClientOnly.preInitClientOnly();
  }

  /**
   * Do your mod setup. Build whatever data structures you care about. Register recipes,
   * send FMLInterModComms messages to other mods.
   */
  public void init()
  {
    super.init();
    divineadditions.trash.mbe70_configuration.StartupClientOnly.initClientOnly();
    
    divineadditions.trash.mbe01_block_simple.StartupClientOnly.initClientOnly();
    divineadditions.trash.mbe02_block_partial.StartupClientOnly.initClientOnly();
    divineadditions.trash.mbe03_block_variants.StartupClientOnly.initClientOnly();
    divineadditions.trash.mbe04_block_dynamic_block_model1.StartupClientOnly.initClientOnly();
    divineadditions.trash.mbe05_block_dynamic_block_model2.StartupClientOnly.initClientOnly();
    divineadditions.trash.mbe06_redstone.StartupClientOnly.initClientOnly();
    divineadditions.trash.mbe08_creative_tab.StartupClientOnly.initClientOnly();
    divineadditions.trash.mbe10_item_simple.StartupClientOnly.initClientOnly();
    divineadditions.trash.mbe11_item_variants.StartupClientOnly.initClientOnly();
    divineadditions.trash.mbe12_item_nbt_animate.StartupClientOnly.initClientOnly();
    divineadditions.trash.mbe13_item_tools.StartupClientOnly.initClientOnly();
    divineadditions.trash.mbe15_item_dynamic_item_model.StartupClientOnly.initClientOnly();
    divineadditions.trash.mbe20_tileentity_data.StartupClientOnly.initClientOnly();
    divineadditions.trash.mbe21_tileentityspecialrenderer.StartupClientOnly.initClientOnly();
    divineadditions.trash.mbe30_inventory_basic.StartupClientOnly.initClientOnly();
    divineadditions.trash.mbe31_inventory_furnace.StartupClientOnly.initClientOnly();
    divineadditions.trash.mbe35_recipes.StartupClientOnly.initClientOnly();
    divineadditions.trash.mbe40_hud_overlay.StartupClientOnly.initClientOnly();
    divineadditions.trash.mbe50_particle.StartupClientOnly.initClientOnly();
    divineadditions.trash.mbe60_network_messages.StartupClientOnly.initClientOnly();
    divineadditions.trash.mbe75_testing_framework.StartupClientOnly.initClientOnly();
    divineadditions.trash.testingarea.StartupClientOnly.initClientOnly();
  }

  /**
   * Handle interaction with other mods, complete your setup based on this.
   */
  public void postInit()
  {
    super.postInit();
    divineadditions.trash.mbe70_configuration.StartupClientOnly.postInitClientOnly();

    divineadditions.trash.mbe01_block_simple.StartupClientOnly.postInitClientOnly();
    divineadditions.trash.mbe02_block_partial.StartupClientOnly.postInitClientOnly();
    divineadditions.trash.mbe03_block_variants.StartupClientOnly.postInitClientOnly();
    divineadditions.trash.mbe04_block_dynamic_block_model1.StartupClientOnly.postInitClientOnly();
    divineadditions.trash.mbe05_block_dynamic_block_model2.StartupClientOnly.postInitClientOnly();
    divineadditions.trash.mbe06_redstone.StartupClientOnly.postInitClientOnly();
    divineadditions.trash.mbe08_creative_tab.StartupClientOnly.postInitClientOnly();
    divineadditions.trash.mbe10_item_simple.StartupClientOnly.postInitClientOnly();
    divineadditions.trash.mbe11_item_variants.StartupClientOnly.postInitClientOnly();
    divineadditions.trash.mbe12_item_nbt_animate.StartupClientOnly.postInitClientOnly();
    divineadditions.trash.mbe13_item_tools.StartupClientOnly.postInitClientOnly();
    divineadditions.trash.mbe15_item_dynamic_item_model.StartupClientOnly.postInitClientOnly();
    divineadditions.trash.mbe20_tileentity_data.StartupClientOnly.postInitClientOnly();
    divineadditions.trash.mbe21_tileentityspecialrenderer.StartupClientOnly.postInitClientOnly();
    divineadditions.trash.mbe30_inventory_basic.StartupClientOnly.postInitClientOnly();
    divineadditions.trash.mbe31_inventory_furnace.StartupClientOnly.postInitClientOnly();
    divineadditions.trash.mbe35_recipes.StartupClientOnly.postInitClientOnly();
    divineadditions.trash.mbe40_hud_overlay.StartupClientOnly.postInitClientOnly();
    divineadditions.trash.mbe50_particle.StartupClientOnly.postInitClientOnly();
    divineadditions.trash.mbe60_network_messages.StartupClientOnly.postInitClientOnly();
    divineadditions.trash.mbe75_testing_framework.StartupClientOnly.postInitClientOnly();
    divineadditions.trash.testingarea.StartupClientOnly.postInitClientOnly();
  }

  @Override
  public boolean playerIsInCreativeMode(EntityPlayer player) {
    if (player instanceof EntityPlayerMP) {
      EntityPlayerMP entityPlayerMP = (EntityPlayerMP)player;
      return entityPlayerMP.interactionManager.isCreative();
    } else if (player instanceof EntityPlayerSP) {
      return Minecraft.getMinecraft().playerController.isInCreativeMode();
    }
    return false;
  }

  @Override
  public boolean isDedicatedServer() {return false;}

}
