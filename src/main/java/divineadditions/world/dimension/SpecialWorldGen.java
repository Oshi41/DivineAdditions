package divineadditions.world.dimension;

import divineadditions.config.DivineAdditionsConfig;
import divineadditions.config.PlanetConfig;
import divineadditions.world.dimension.planet.PlanetWorldGen;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class SpecialWorldGen implements IWorldGenerator {
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {

        // always at chunk start
        BlockPos pos = new ChunkPos(chunkX, chunkZ).getBlock(16, 0, 16);

        for (int i = 0; i < DivineAdditionsConfig.planetDimensionConfig.spawnTries; i++) {
            int radius = random.nextInt(15 - DivineAdditionsConfig.planetDimensionConfig.minRadius) + DivineAdditionsConfig.planetDimensionConfig.minRadius;
            int freeZone = (30 - radius * 2) / 2;

            pos = pos.add(0, random.nextInt(150) + 80, 0);

            if (freeZone > 1) {
                pos = pos.add(
                        random.nextInt(freeZone) - freeZone,
                        0,
                        random.nextInt(freeZone) - freeZone
                );
            }

            new PlanetWorldGen(this::createRandom, radius, true).generate(world, random, pos);
        }
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
}
