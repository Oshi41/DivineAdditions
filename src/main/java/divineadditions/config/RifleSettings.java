package divineadditions.config;

import net.minecraft.init.Items;
import net.minecraftforge.common.config.Config;

public class RifleSettings {
    @Config.Comment("Reagent for rifle to shoot the bullet. Write MOD_ID:ITEM_NAME to use modded items")
    public String rifleCatalyst = Items.GUNPOWDER.getRegistryName().getResourcePath();

    @Config.RangeInt(min = 1, max = 64)
    @Config.Comment("How much catalyst we need to fire a bullet")
    public Integer rifleCatalystCount = 1;

    @Config.RangeInt(min = 5, max = 20)
    @Config.Comment("Cooldown beetween shots")
    public Integer rifleCooldown = 5;

    @Config.RangeInt(min = 100, max = 10000)
    @Config.Comment("Maximum rifle durability")
    public Integer rifleDurability = 824;
}
