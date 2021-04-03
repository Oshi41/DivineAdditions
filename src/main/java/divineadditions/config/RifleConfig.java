package divineadditions.config;

import net.minecraftforge.common.config.Config;

public class RifleConfig {

    @Config.Comment("Settings for mob rifle core")
    public RifleMobCore mobCoreConfig = new RifleMobCore();

    @Config.Comment("Settings for bullet rifle core")
    public RifleBulletCore bulletCoreConfig = new RifleBulletCore();
}
