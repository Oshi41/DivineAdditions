package divineadditions.world.dimension.planet;

import divineadditions.DivineAdditions;
import divineadditions.config.DivineAdditionsConfig;
import divineadditions.config.PlanetConfig;
import divineadditions.utils.StructureUtils;
import divineadditions.world.gen.WorldGenVines;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class PlanetBiome extends Biome {
    public static final ResourceLocation ancientPortalId = new ResourceLocation(DivineAdditions.MOD_ID, "ancient_portal");

    public PlanetBiome() {
        super(new BiomeProperties("Planets").setTemperature(0.8f).setRainfall(0.5f));
    }

    private PlanetConfig createRandom(Random random) {
        List<PlanetConfig> possiblePlanets = DivineAdditionsConfig
                .planetDimensionConfig
                .possiblePlanets
                .stream()
                .filter(x -> random.nextInt(x.getProbability()) == 0)
                .collect(Collectors.toList());

        if (possiblePlanets.isEmpty())
            return null;

        return possiblePlanets.get(random.nextInt(possiblePlanets.size()));
    }

    @Override
    public void decorate(World worldIn, Random rand, BlockPos chunkStart) {
        if (worldIn instanceof WorldServer) {
            StructureUtils.StructureInfo info = StructureUtils.readFromNbt((WorldServer) worldIn, ancientPortalId);
            if (info != null) {
                AncientPortalGen gen = new AncientPortalGen(info.getTemplate());
                int min = 20;

                BlockPos portalpos = chunkStart.up(rand.nextInt(worldIn.getHeight() - min * 2) + min);
                gen.generate(worldIn, rand, portalpos);

                for (int k = 0; k < DivineAdditionsConfig.planetDimensionConfig.spawnTries; k++) {
                    new PlanetWorldGen(this::createRandom, true).generate(worldIn, worldIn.rand, chunkStart);
                }
            }
        }

        decorator.sandPatchesPerChunk = 0;
        decorator.gravelPatchesPerChunk = 0;
        decorator.clayPerChunk = 0;
        decorator.grassPerChunk = 0;
        decorator.bigMushroomsPerChunk = 1;
        decorator.cactiPerChunk = 1;
        decorator.reedsPerChunk = 1;
        decorator.mushroomsPerChunk = 1;
        decorator.generateFalls = false;
        decorator.treesPerChunk = 1;
        decorator.flowersPerChunk = 8;
        decorator.deadBushPerChunk = 2;
        decorator.waterlilyPerChunk = 2;

        this.decorator.decorate(worldIn, rand, this, chunkStart);

        WorldGenVines worldgenvines = new WorldGenVines();

        for (int i = 0; i < 50; i++) {
            worldgenvines.generate(worldIn, rand, chunkStart.add(
                    rand.nextInt(16) + 8,
                    rand.nextInt(50) + 20,
                    rand.nextInt(16) + 8
            ));
        }
    }
}
