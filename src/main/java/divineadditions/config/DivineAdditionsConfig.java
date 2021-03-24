package divineadditions.config;

import divineadditions.DivineAdditions;
import net.minecraftforge.common.config.Config;

import java.util.HashMap;
import java.util.Map;

@Config(modid = DivineAdditions.MOD_ID, type = Config.Type.INSTANCE, name = DivineAdditions.MOD_ID + "/main")
public class DivineAdditionsConfig {
    @Config.Comment("Settings for mob rifle")
    public static RifleMobCore rifleMobCore = new RifleMobCore();

    @Config.Comment("Entities configuration")
    public static MobConfig mobsConfig = new MobConfig();

    @Config.Comment("Config for planet dimension")
    public static PlanetDimensionConfig planetDimensionConfig = new PlanetDimensionConfig();

    @Config.Comment("Gravity for worlds")
    public static Map<String, Double> gravity = new HashMap<String, Double>() {{
        put("overworld", 1.);
        put(DivineAdditions.MOD_ID + ":planets", 0.5);
    }};
}
