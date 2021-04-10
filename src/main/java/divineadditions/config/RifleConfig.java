package divineadditions.config;

import net.minecraftforge.common.config.Config;

public class RifleConfig {

    @Config.Comment("Settings for mob rifle core")
    public RifleMobCore mobCoreConfig = new RifleMobCore();

    @Config.Comment("Settings for bullet rifle core")
    public RifleBulletCore bulletCoreConfig = new RifleBulletCore();

    @Config.Comment("Durability for rifle")
    @Config.RangeInt(min = 1)
    public int durability = 1824;
}
