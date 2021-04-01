package divineadditions.world.dimension.planet;

import divineadditions.config.DivineAdditionsConfig;
import divineadditions.config.PlanetConfig;
import divineadditions.utils.FakeWorld;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.IChunkGenerator;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class PlanetsChunkGen implements IChunkGenerator {
    private World world;
    private FakeWorld fakeWorld;
    private Biome[] biomesForGeneration;

    public PlanetsChunkGen(World world) {
        this.world = world;
        fakeWorld = new FakeWorld(world);
    }

    @Override
    public Chunk generateChunk(int x, int z) {
        generatePlanets(x, z);

        Chunk chunk = new Chunk(world, fakeWorld.createFrom(new ChunkPos(x, z)), x, z);
        chunk.generateSkylightMap();

        byte[] abyte = chunk.getBiomeArray();
        this.biomesForGeneration = this.world.getBiomeProvider().getBiomes(this.biomesForGeneration, x * 16, z * 16, 16, 16);

        for (int i = 0; i < abyte.length; ++i) {
            abyte[i] = (byte) Biome.getIdForBiome(this.biomesForGeneration[i]);
        }

        return chunk;
    }

    @Override
    public void populate(int x, int z) {
        BlockFalling.fallInstantly = true;
        net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(true, this, this.world, this.world.rand, x, z, false);
        int i = x * 16;
        int j = z * 16;
        BlockPos blockpos = new BlockPos(i, 0, j);
        Biome biome = this.world.getBiome(blockpos.add(16, 0, 16));
        biome.decorate(this.world, this.world.rand, new BlockPos(i, 0, j));
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.terraingen.DecorateBiomeEvent.Post(this.world, this.world.rand, blockpos));
        BlockFalling.fallInstantly = false;
    }

    @Override
    public boolean generateStructures(Chunk chunkIn, int x, int z) {
        return false;
    }

    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        return null;
    }

    @Nullable
    @Override
    public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {
        return null;
    }

    @Override
    public void recreateStructures(Chunk chunkIn, int x, int z) {

    }

    @Override
    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
        return false;
    }

    private boolean generatePlanets(int x, int z) {
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                ChunkPos chunkPos = new ChunkPos(x + i, z + j);

                if (!fakeWorld.isBlockLoaded(chunkPos.getBlock(0, 0, 0))) {
                    BlockPos chunkStart = chunkPos.getBlock(0, 0, 0);
                    for (int k = 0; k < DivineAdditionsConfig.planetDimensionConfig.spawnTries; k++) {
                        new PlanetWorldGen(this::createRandom, true).generate(fakeWorld, world.rand, chunkStart);
                    }
                }
            }
        }


        return true;
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
}
