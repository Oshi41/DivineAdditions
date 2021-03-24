package divineadditions.world.dimension.planet;

import divineadditions.config.DivineAdditionsConfig;
import divineadditions.config.PlanetConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class PlanetBiome extends Biome {

    public PlanetBiome() {
        super(new BiomeProperties("Planets").setTemperature(0.3f).setRainfall(0.5f));
    }

    private PlanetConfig createRandom(Random random) {
        List<PlanetConfig> possiblePlanets = DivineAdditionsConfig
                .planetDimensionConfig
                .possiblePlanets
                .stream()
                .filter(x -> random.nextInt(x.probability) == 0)
                .collect(Collectors.toList());

        if (possiblePlanets.isEmpty())
            return null;

        return possiblePlanets.get(random.nextInt(possiblePlanets.size()));
    }

    @Override
    public void decorate(World worldIn, Random rand, BlockPos pos) {

        // always at chunk start
        final BlockPos chunkStart = new ChunkPos(pos).getBlock(16, 0, 16);

        for (int i = 0; i < DivineAdditionsConfig.planetDimensionConfig.spawnTries; i++) {
            int radius = rand.nextInt(15 - DivineAdditionsConfig.planetDimensionConfig.minRadius) + DivineAdditionsConfig.planetDimensionConfig.minRadius;
            int freeZone = (30 - radius * 2) / 2;
            BlockPos currentPos = chunkStart.add(0, rand.nextInt(200) + 20, 0);

            if (freeZone > 1) {
                currentPos = currentPos.add(rand.nextInt(freeZone) - freeZone, 0, rand.nextInt(freeZone) - freeZone);
            }

            new PlanetWorldGen(this::createRandom, radius, true).generate(worldIn, rand, currentPos);
        }
    }
}
