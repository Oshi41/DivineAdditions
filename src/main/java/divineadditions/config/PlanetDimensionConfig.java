package divineadditions.config;

import net.minecraft.init.Blocks;
import net.minecraftforge.common.config.Config;

import java.util.Arrays;
import java.util.List;

public class PlanetDimensionConfig {
    @Config.Ignore
    public List<PlanetConfig> possiblePlanets = Arrays.asList(
            new PlanetConfig(Blocks.STONE, Blocks.STONE, Blocks.STONE, Blocks.STONE, 5),
            new PlanetConfig(Blocks.GLOWSTONE, Blocks.GLOWSTONE, Blocks.GLOWSTONE, Blocks.GLOWSTONE, 125),
            new PlanetConfig(Blocks.GRAVEL, Blocks.STONE, Blocks.GRAVEL, Blocks.GRAVEL, 5),
            new PlanetConfig(Blocks.LEAVES, Blocks.LEAVES, Blocks.LOG, Blocks.LEAVES, 25),
            new PlanetConfig(Blocks.GRASS, Blocks.STONE, Blocks.DIRT, Blocks.DIRT, 5),
            new PlanetConfig(Blocks.GLASS, Blocks.GLASS, Blocks.WATER, Blocks.GLASS, 35)
    );

    @Config.Comment("Min radius for planet generation")
    @Config.RangeInt(min = 5, max = 14)
    public int minRadius = 5;

    @Config.Comment("How often planets will spawn")
    @Config.RangeInt(min = 1, max = 100)
    public int spawnTries = 16;

    @Config.Comment("dimension ID")
    public int id = 14;
}
