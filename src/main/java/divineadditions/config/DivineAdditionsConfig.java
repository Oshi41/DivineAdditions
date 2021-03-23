package divineadditions.config;

import divineadditions.DivineAdditions;
import net.minecraftforge.common.config.Config;

@Config(modid = DivineAdditions.MOD_ID, type = Config.Type.INSTANCE)
public class DivineAdditionsConfig {
    @Config.Comment("Settings for mob rifle")
    public static RifleMobCore rifleMobCore = new RifleMobCore();

    @Config.Comment("Entities configuration")
    public static MobConfig mobsConfig = new MobConfig();

    @Config.Comment("Config for planet dimension")
    public static PlanetDimensionConfig planetDimensionConfig = new PlanetDimensionConfig();
}
