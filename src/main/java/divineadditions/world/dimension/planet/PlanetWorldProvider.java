package divineadditions.world.dimension.planet;

import divineadditions.holders.Biomes;
import divineadditions.holders.Dimensions;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;

public class PlanetWorldProvider extends WorldProvider {

    public PlanetWorldProvider() {
    }

    @Override
    protected void init() {
        hasSkyLight = false;
        this.biomeProvider = new BiomeProviderSingle(Biomes.planet);
    }

    @Override
    public DimensionType getDimensionType() {
        return Dimensions.planetDimension;
    }

    @Override
    public boolean isSurfaceWorld() {
        return false;
    }

    @Override
    public IChunkGenerator createChunkGenerator() {
        return new EmptyChunkGenerator(world);
    }

    /**
     * Calculates the angle of sun and moon in the sky relative to a specified time (usually worldTime)
     */
    @Override
    public float calculateCelestialAngle(long worldTime, float partialTicks) {
        return 0.5F;
    }

    @Override
    protected void generateLightBrightnessTable() {
        float f = 0.1F;
        for (int i = 0; i <= 15; ++i) {
            float f1 = 1.0F - (float) i / 15.0F; // 1 - 0
            this.lightBrightnessTable[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * 0.5F + 0.5F; // 0.5 - 1
        }
    }
}
