package divineadditions.world.gen;

import divineadditions.config.PlanetConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;
import java.util.function.Function;

public class PlanetWorldGen extends WorldGenerator {
    private Function<Random, PlanetConfig> configSupplier;
    private Function<Random, Integer> raduisFunc;

    public PlanetWorldGen(Function<Random, PlanetConfig> config, Function<Random, Integer> raduisFunc) {
        this.configSupplier = config;
        this.raduisFunc = raduisFunc;
    }

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        PlanetConfig config = configSupplier.apply(rand);
        if (config == null)
            return false;

        int radius = raduisFunc.apply(rand);

        for (int x2 = position.getX() - radius; x2 <= position.getX() + radius; x2++) {
            for (int y2 = position.getY() - radius; y2 <= position.getY() + radius; y2++) {
                for (int z2 = position.getZ() - radius; z2 <= position.getZ() + radius; z2++) {
                    BlockPos currentPos = new BlockPos(x2, y2, z2);

                    int d = roundDistance(currentPos, position);

                    if (d == radius) {
                        if (isBottomBlock(currentPos, position, radius)) {
                            setBlockAndNotifyAdequately(worldIn, currentPos, config.getBottom());
                        } else if (isTopBlock(currentPos, position, radius)) {
                            setBlockAndNotifyAdequately(worldIn, currentPos, config.getTop());
                        } else {
                            setBlockAndNotifyAdequately(worldIn, currentPos, config.getOut());
                        }
                    } else if (d < radius) {
                        setBlockAndNotifyAdequately(worldIn, currentPos, config.getIn());
                    }
                }
            }
        }

        return true;
    }

    private boolean isTopBlock(BlockPos pos, BlockPos originalPos, int radius) {
        return roundDistance(pos, originalPos) == radius
                && roundDistance(pos.up(), originalPos) > radius;
    }

    private boolean isBottomBlock(BlockPos pos, BlockPos originalPos, int radius) {
        return roundDistance(pos, originalPos) == radius
                && roundDistance(pos.down(), originalPos) > radius;
    }

    private int roundDistance(BlockPos first, BlockPos second) {
        return (int) Math.round(Math.sqrt(first.distanceSq(second)));
    }
}
