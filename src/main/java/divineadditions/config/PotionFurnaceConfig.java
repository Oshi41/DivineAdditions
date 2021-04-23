package divineadditions.config;

import divinerpg.DivineRPG;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;

import java.util.HashMap;
import java.util.Map;

public class PotionFurnaceConfig {
    @Config.Comment("Fuel for potion furnace")
    public Map<String, Integer> potionFurnaceFuel = new HashMap<String, Integer>() {{
        put(new ResourceLocation(DivineRPG.MODID, "purple_blaze").toString(), 100);
    }};

    @Config.Comment("Max potion effects can be applied on single sword")
    @Config.RangeInt(min = 1)
    public int maxPotionsCount = 6;
}
