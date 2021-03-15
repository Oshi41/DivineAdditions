package divineadditions.trash;

import net.minecraft.entity.player.EntityPlayer;

/**
 * CommonProxy is used to set up the mod and start it running.  It contains all the code that should run on both the
 *   Standalone client and the dedicated server.
 *   For more background information see here http://greyminecraftcoder.blogspot.com/2013/11/how-forge-starts-up-your-code.html
 */
public abstract class CommonProxy {

  /**
   * Run before anything else. Read your config, create blocks, items, etc, and register them with the GameRegistry
   */
  public void preInit()
  {
	   //read config first
	  divineadditions.trash.mbe70_configuration.StartupCommon.preInitCommon();

    divineadditions.trash.mbe01_block_simple.StartupCommon.preInitCommon();
    divineadditions.trash.mbe02_block_partial.StartupCommon.preInitCommon();
    divineadditions.trash.mbe03_block_variants.StartupCommon.preInitCommon();
    divineadditions.trash.mbe04_block_dynamic_block_model1.StartupCommon.preInitCommon();
    divineadditions.trash.mbe05_block_dynamic_block_model2.StartupCommon.preInitCommon();
    divineadditions.trash.mbe06_redstone.StartupCommon.preInitCommon();
    divineadditions.trash.mbe08_creative_tab.StartupCommon.preInitCommon();
    divineadditions.trash.mbe10_item_simple.StartupCommon.preInitCommon();
    divineadditions.trash.mbe11_item_variants.StartupCommon.preInitCommon();
    divineadditions.trash.mbe12_item_nbt_animate.StartupCommon.preInitCommon();
    divineadditions.trash.mbe13_item_tools.StartupCommon.preInitCommon();
    divineadditions.trash.mbe15_item_dynamic_item_model.StartupCommon.preInitCommon();
    divineadditions.trash.mbe20_tileentity_data.StartupCommon.preInitCommon();
    divineadditions.trash.mbe21_tileentityspecialrenderer.StartupCommon.preInitCommon();
    divineadditions.trash.mbe30_inventory_basic.StartupCommon.preInitCommon();
    divineadditions.trash.mbe31_inventory_furnace.StartupCommon.preInitCommon();
    divineadditions.trash.mbe35_recipes.StartupCommon.preInitCommon();
    divineadditions.trash.mbe40_hud_overlay.StartupCommon.preInitCommon();
    divineadditions.trash.mbe50_particle.StartupCommon.preInitCommon();
    divineadditions.trash.mbe60_network_messages.StartupCommon.preInitCommon();
    divineadditions.trash.mbe75_testing_framework.StartupCommon.preInitCommon();
    divineadditions.trash.testingarea.StartupCommon.preInitCommon();
  }

  /**
   * Do your mod setup. Build whatever data structures you care about. Register recipes,
   * send FMLInterModComms messages to other mods.
   */
  public void init()
  {
	divineadditions.trash.mbe70_configuration.StartupCommon.initCommon();
	  
    divineadditions.trash.mbe01_block_simple.StartupCommon.initCommon();
    divineadditions.trash.mbe02_block_partial.StartupCommon.initCommon();
    divineadditions.trash.mbe03_block_variants.StartupCommon.initCommon();
    divineadditions.trash.mbe04_block_dynamic_block_model1.StartupCommon.initCommon();
    divineadditions.trash.mbe05_block_dynamic_block_model2.StartupCommon.initCommon();
    divineadditions.trash.mbe06_redstone.StartupCommon.initCommon();
    divineadditions.trash.mbe08_creative_tab.StartupCommon.initCommon();
    divineadditions.trash.mbe10_item_simple.StartupCommon.initCommon();
    divineadditions.trash.mbe11_item_variants.StartupCommon.initCommon();
    divineadditions.trash.mbe12_item_nbt_animate.StartupCommon.initCommon();
    divineadditions.trash.mbe13_item_tools.StartupCommon.initCommon();
    divineadditions.trash.mbe15_item_dynamic_item_model.StartupCommon.initCommon();
    divineadditions.trash.mbe20_tileentity_data.StartupCommon.initCommon();
    divineadditions.trash.mbe21_tileentityspecialrenderer.StartupCommon.initCommon();
    divineadditions.trash.mbe30_inventory_basic.StartupCommon.initCommon();
    divineadditions.trash.mbe31_inventory_furnace.StartupCommon.initCommon();
    divineadditions.trash.mbe35_recipes.StartupCommon.initCommon();
    divineadditions.trash.mbe40_hud_overlay.StartupCommon.initCommon();
    divineadditions.trash.mbe50_particle.StartupCommon.initCommon();
    divineadditions.trash.mbe60_network_messages.StartupCommon.initCommon();
    divineadditions.trash.mbe75_testing_framework.StartupCommon.initCommon();
//    minecraftbyexample.testingarea.StartupCommon.initCommon();
  }

  /**
   * Handle interaction with other mods, complete your setup based on this.
   */
  public void postInit()
  {
	divineadditions.trash.mbe70_configuration.StartupCommon.postInitCommon();
	  
    divineadditions.trash.mbe01_block_simple.StartupCommon.postInitCommon();
    divineadditions.trash.mbe02_block_partial.StartupCommon.postInitCommon();
    divineadditions.trash.mbe03_block_variants.StartupCommon.postInitCommon();
    divineadditions.trash.mbe04_block_dynamic_block_model1.StartupCommon.postInitCommon();
    divineadditions.trash.mbe05_block_dynamic_block_model2.StartupCommon.postInitCommon();
    divineadditions.trash.mbe06_redstone.StartupCommon.postInitCommon();
    divineadditions.trash.mbe08_creative_tab.StartupCommon.postInitCommon();
    divineadditions.trash.mbe10_item_simple.StartupCommon.postInitCommon();
    divineadditions.trash.mbe11_item_variants.StartupCommon.postInitCommon();
    divineadditions.trash.mbe12_item_nbt_animate.StartupCommon.postInitCommon();
    divineadditions.trash.mbe13_item_tools.StartupCommon.postInitCommon();
    divineadditions.trash.mbe15_item_dynamic_item_model.StartupCommon.postInitCommon();
    divineadditions.trash.mbe20_tileentity_data.StartupCommon.postInitCommon();
    divineadditions.trash.mbe21_tileentityspecialrenderer.StartupCommon.postInitCommon();
    divineadditions.trash.mbe30_inventory_basic.StartupCommon.postInitCommon();
    divineadditions.trash.mbe31_inventory_furnace.StartupCommon.postInitCommon();
    divineadditions.trash.mbe35_recipes.StartupCommon.postInitCommon();
    divineadditions.trash.mbe40_hud_overlay.StartupCommon.postInitCommon();
    divineadditions.trash.mbe50_particle.StartupCommon.postInitCommon();
    divineadditions.trash.mbe60_network_messages.StartupCommon.postInitCommon();
    divineadditions.trash.mbe75_testing_framework.StartupCommon.postInitCommon();
    divineadditions.trash.testingarea.StartupCommon.postInitCommon();
  }

  // helper to determine whether the given player is in creative mode
  //  not necessary for most examples
  abstract public boolean playerIsInCreativeMode(EntityPlayer player);

  /**
   * is this a dedicated server?
   * @return true if this is a dedicated server, false otherwise
   */
  abstract public boolean isDedicatedServer();
}
