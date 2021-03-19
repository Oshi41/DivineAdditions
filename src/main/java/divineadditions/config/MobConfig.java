package divineadditions.config;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraftforge.common.config.Config;

import java.util.HashMap;
import java.util.Map;

public class MobConfig {
    @Config.Comment("Base attributes for Armor Defender boss entity. Other summonses from one player and armor can increase its characteristics")
    public Map<String, Float> armor_defender_attrs = new HashMap<String, Float>() {{
        put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), 0.4f);
        put(SharedMonsterAttributes.MAX_HEALTH.getName(), 100f);
        put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), 10f);
        put(SharedMonsterAttributes.ATTACK_SPEED.getName(), 4.5F);
        put(SharedMonsterAttributes.ARMOR.getName(), 2f);
        put(SharedMonsterAttributes.FOLLOW_RANGE.getName(), 128f);
    }};
}
