package divineadditions.config;

import divineadditions.DivineAdditions;
import divineadditions.api.IRifleCoreConfig;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;

import java.util.HashMap;
import java.util.Map;

public class RifleMobCore implements IRifleCoreConfig {
    @Config.RangeInt
    @Config.Comment("rifle mob core durability")
    public Integer durability = 300;

    @Config.Comment("Bullets that current core can apply. Number means amount of ammo using per shot")
    public Map<String, Integer> bullets = new HashMap<String, Integer>() {{
        put(new ResourceLocation(DivineAdditions.MOD_ID, "soul_powder").toString(), 1);
    }};

    @Config.RangeInt(min = 15, max = 60)
    @Config.Comment("Core cooldown")
    public Integer cooldown = 15;

    @Config.Comment("Catalyst for shot core can apply. Number means amount of ammo using per shot")
    public Map<String, Integer> catalyst = new HashMap<String, Integer>() {{
        put(net.minecraft.init.Items.GUNPOWDER.getRegistryName().toString(), 3);
        put(net.minecraft.init.Blocks.TNT.getRegistryName().toString(), 1);
    }};

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
}
