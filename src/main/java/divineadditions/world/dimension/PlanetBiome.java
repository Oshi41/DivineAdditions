package divineadditions.world.dimension;

import divineadditions.DivineAdditions;
import divineadditions.config.DivineAdditionsConfig;
import divineadditions.config.PlanetConfig;
import divineadditions.world.gen.PlanetWorldGen;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class PlanetBiome extends Biome {
    protected WorldGenerator planetGenerator;

    public PlanetBiome(BiomeProperties properties) {
        super(properties);
        setRegistryName(DivineAdditions.MOD_ID, "planet");
        planetGenerator = new PlanetWorldGen(this::createRandom, this::createRandomSize);
    }

    private Integer createRandomSize(Random random) {
        int min = DivineAdditionsConfig.planetDimensionConfig.minRadius;

        int max = 16 - min;

        return random.nextInt(max) + min;
    }

    private PlanetConfig createRandom(Random random) {
        List<PlanetConfig> possiblePlanets = Arrays
                .stream(DivineAdditionsConfig.planetDimensionConfig.possiblePlanets)
                .filter(x -> random.nextInt(x.probability) == 0)
                .collect(Collectors.toList());

        if (possiblePlanets.isEmpty())
            return null;

        return possiblePlanets.get(random.nextInt(possiblePlanets.size()));
    }

    @Override
    public void decorate(World worldIn, Random rand, BlockPos pos) {

    }
}
