package divineadditions.config;

import divineadditions.DivineAdditions;
import net.minecraftforge.common.config.Config;

import java.util.HashMap;
import java.util.Map;

@Config(modid = DivineAdditions.MOD_ID, type = Config.Type.INSTANCE, name = DivineAdditions.MOD_ID + "/main")
public class DivineAdditionsConfig {
    @Config.Comment("Settings for rifle")
    public static RifleConfig rifleConfig = new RifleConfig();

    @Config.Comment({
            "Config for planet dimension",
            "Plants config are placing in plants.json file",
            "top - block registry name for top block on planets",
            "bottom - block registry name for bottom block on planets",
            "in - block registry name for blocks inside a planets",
            "side - block registry name for side blocks of planets",
            "probability - from 1 and higher. Counting as 1/x, so 100 is 10 times rarer than 10",
            "yMin - Min y pos for current planet type. >= 0 obviously",
            "yMax - Max y pos for planet spawn. Not higher than world height (256)",
            "minRadius - Min radius for current planet type. >= 2",
            "maxRadius - Max radius for current planet type. <= 15 and >= minRadius obviously",
            "alwaysHalf - if true sky block like planet will spawn",
    })
    public static PlanetDimensionConfig planetDimensionConfig = new PlanetDimensionConfig();

    @Config.Comment("Gravity for worlds")
    public static Map<String, Double> gravity = new HashMap<String, Double>() {{
        put(DivineAdditions.MOD_ID + ":planets", 0.25);
    }};

    @Config.Comment("Section for potion furnace config")
    public static PotionFurnaceConfig potionFurnaceConfig = new PotionFurnaceConfig();
}
