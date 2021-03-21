package divineadditions.config;

import divinerpg.DivineRPG;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MobConfig {
    @Config.Comment("Base attributes for Armor Defender boss entity. Other summonses from one player and armor can increase its characteristics")
    public Map<String, Float> armor_defender_attrs = new HashMap<String, Float>() {{
        put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), 0.23000000417232513F);
        put(SharedMonsterAttributes.MAX_HEALTH.getName(), 500F);
        put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), 20f);
        put(SharedMonsterAttributes.ATTACK_SPEED.getName(), 4.0F);
        put(SharedMonsterAttributes.ARMOR.getName(), 2f);
        put(SharedMonsterAttributes.FOLLOW_RANGE.getName(), 128f);
    }};

    @Config.Comment("Armor set ids for Armor Defender boss that gives ability to fly")
    public Set<String> aerSets = new HashSet<String>() {{
        add(new ResourceLocation(DivineRPG.MODID, "angelic").toString());
    }};

    @Config.Comment("Armor set ids for Armor Defender boss that enpowers him in water")
    public Set<String> waterSets = new HashSet<String>() {{
        add(new ResourceLocation(DivineRPG.MODID, "aqua").toString());
        add(new ResourceLocation(DivineRPG.MODID, "wildwood").toString());
        add(new ResourceLocation(DivineRPG.MODID, "kraken").toString());
    }};

    @Config.Comment("Armor set ids for Armor Defender boss that gives immunity to fire")
    public Set<String> fireSets = new HashSet<String>() {{
        add(new ResourceLocation(DivineRPG.MODID, "bedrock").toString());
        add(new ResourceLocation(DivineRPG.MODID, "netherite").toString());
        add(new ResourceLocation(DivineRPG.MODID, "inferno").toString());
    }};
}
