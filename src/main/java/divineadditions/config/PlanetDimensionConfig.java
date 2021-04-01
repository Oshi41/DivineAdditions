package divineadditions.config;

import net.minecraft.init.Blocks;
import net.minecraftforge.common.config.Config;

import java.util.Arrays;
import java.util.List;

public class PlanetDimensionConfig {
    @Config.Ignore
    public List<PlanetConfig> possiblePlanets = Arrays.asList(
            new PlanetConfig(Blocks.STONE, Blocks.STONE, Blocks.STONE, Blocks.STONE, 5, 20, 80, 4, 15),
            new PlanetConfig(Blocks.GRAVEL, Blocks.STONE, Blocks.DIRT, Blocks.STONE, 5, 20, 150, 4, 15),
            new PlanetConfig(Blocks.LEAVES, Blocks.LEAVES, Blocks.LOG, Blocks.LEAVES, 25, 80, 230, 5, 7),
            new PlanetConfig(Blocks.GRASS, Blocks.STONE, Blocks.DIRT, Blocks.DIRT, 4, 80, 230, 4, 10),
            new PlanetConfig(Blocks.AIR, Blocks.GLASS, Blocks.WATER, Blocks.GLASS, 125, 150, 200, 7, 10, true),
            new PlanetConfig(Blocks.CLAY, Blocks.CLAY, Blocks.CLAY, Blocks.CLAY, 40, 80, 120, 3, 7),
            new PlanetConfig(Blocks.SAND, Blocks.SANDSTONE, Blocks.SAND, Blocks.SANDSTONE, 40, 80, 150, 7, 15),
            new PlanetConfig(Blocks.GLOWSTONE, Blocks.GLOWSTONE, Blocks.GLOWSTONE, Blocks.GLOWSTONE, 200, 150, 240, 3, 7),
            new PlanetConfig(Blocks.SOUL_SAND, Blocks.NETHERRACK, Blocks.SOUL_SAND, Blocks.NETHERRACK, 230, 20, 240, 3, 7),
            new PlanetConfig(Blocks.END_STONE, Blocks.END_STONE, Blocks.END_STONE, Blocks.END_STONE, 230, 20, 240, 12, 15)
    );

    @Config.Comment("How often planets will spawn")
    @Config.RangeInt(min = 1, max = 100)
    public int spawnTries = 18;

    @Config.Comment("dimension ID")
    public int id = 14;

    @Config.Comment("Spawn chance for ancient portal. Counting as 1/x for chunk.")
    @Config.RangeInt(min = 1)
    public int portalSpawnChance = 50;
}
