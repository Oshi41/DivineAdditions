package divineadditions.config;

import divineadditions.DivineAdditions;
import net.minecraftforge.common.config.Config;

@Config(modid = DivineAdditions.MOD_ID, type = Config.Type.INSTANCE)
public class DivineAdditionsConfig {
    @Config.Comment("Settings for mob rifle")
    public static RifleSettings rifleSettings = new RifleSettings();

}
