package divineadditions.config;

import divineadditions.DivineAdditions;
import divinerpg.DivineRPG;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Config(modid = DivineAdditions.MOD_ID, type = Config.Type.INSTANCE, name = DivineAdditions.MOD_ID + "/mobs")
public class MobConfig {
    @Config.Comment("Base attributes for Armor Defender boss entity. Other summonses from one player and armor can increase its characteristics")
    public static Map<String, Float> armor_defender_attrs = new HashMap<String, Float>() {{
        put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), 0.23000000417232513F);
        put(SharedMonsterAttributes.MAX_HEALTH.getName(), 500F);
        put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), 20f);
        put(SharedMonsterAttributes.ATTACK_SPEED.getName(), 4.0F);
        put(SharedMonsterAttributes.ARMOR.getName(), 2f);
        put(SharedMonsterAttributes.FOLLOW_RANGE.getName(), 128f);
    }};

    @Config.Comment("Armor set ids for Armor Defender boss that gives ability to fly")
    public static String[] aerSets = Arrays.asList(
            new ResourceLocation(DivineRPG.MODID, "angelic").toString())
            .toArray(new String[0]);

    @Config.Comment("Armor set ids for Armor Defender boss that enpowers him in water")
    public static String[] waterSets = Arrays.asList(
            new ResourceLocation(DivineRPG.MODID, "aqua").toString(),
            new ResourceLocation(DivineRPG.MODID, "wildwood").toString(),
            new ResourceLocation(DivineRPG.MODID, "kraken").toString())
            .toArray(new String[0]);

    @Config.Comment("Armor set ids for Armor Defender boss that gives immunity to fire")
    public static String[] fireSets = Arrays.asList(
            new ResourceLocation(DivineRPG.MODID, "bedrock").toString(),
            new ResourceLocation(DivineRPG.MODID, "netherite").toString(),
            new ResourceLocation(DivineRPG.MODID, "inferno").toString())
            .toArray(new String[0]);

    @Config.Comment("Base attributes for ancient villager")
    public static Map<String, Float> ancient_villager_attrs = new HashMap<String, Float>() {{
        put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), 0.5f);
        put(SharedMonsterAttributes.MAX_HEALTH.getName(), 80f);
        put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), 1f);
        put(SharedMonsterAttributes.ATTACK_SPEED.getName(), 4.0F);
        put(SharedMonsterAttributes.ARMOR.getName(), 2f);
        put(SharedMonsterAttributes.FOLLOW_RANGE.getName(), 32f);
    }};
}
