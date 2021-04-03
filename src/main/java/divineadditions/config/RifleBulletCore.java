package divineadditions.config;

import divineadditions.DivineAdditions;
import divineadditions.api.IRifleCoreConfig;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RifleBulletCore implements IRifleCoreConfig {
    @Config.RangeInt
    @Config.Comment("rifle bullet core durability")
    public Integer durability = 900;

    @Config.Comment("Bullets that current core can apply. Number means amount of ammo using per shot")
    public Map<String, Integer> bullets = new HashMap<String, Integer>() {{
        put(new ResourceLocation(DivineAdditions.MOD_ID, "rifle_bullet").toString(), 1);
    }};

    @Config.RangeInt(min = 1, max = 60)
    @Config.Comment("Core cooldown")
    public Integer cooldown = 5;


    @Config.RangeInt(min = 1)
    @Config.Comment("Base core damage amount")
    public Integer bulletDamage = 35;

    @Config.Comment("Catalyst for shot core can apply. Number means amount of ammo using per shot")
    public Map<String, Integer> catalyst = new HashMap<String, Integer>() {{
        put(net.minecraft.init.Items.GUNPOWDER.getRegistryName().toString(), 1);
    }};

    @Config.Comment("Repair items for current core")
    public String[] repair = Arrays.asList(
            Items.IRON_INGOT.toString()
    ).toArray(new String[0]);

    @Override
    public int getDurability() {
        return durability;
    }

    @Override
    public int getCoolddown() {
        return cooldown;
    }

    @Override
    public Map<String, Integer> getBullets() {
        return bullets;
    }

    @Override
    public Map<String, Integer> getCatalysts() {
        return catalyst;
    }

    @Override
    public String[] getRepairItems() {
        return repair;
    }
}
