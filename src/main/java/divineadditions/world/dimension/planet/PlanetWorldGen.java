package divineadditions.world.dimension.planet;

import divineadditions.config.PlanetConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.StreamSupport;

public class PlanetWorldGen extends WorldGenerator {
    private Function<Random, PlanetConfig> configSupplier;
    private boolean checkIsFree;

    public PlanetWorldGen(Function<Random, PlanetConfig> config, boolean checkIsFree) {
        this.configSupplier = config;
        this.checkIsFree = checkIsFree;
    }

    public static boolean checkIsFree(World worldIn, BlockPos position, int radius) {
        return StreamSupport.stream(BlockPos.getAllInBoxMutable(position.add(-radius, -radius, -radius),
                position.add(radius, radius, radius)).spliterator(), false)
                .allMatch(worldIn::isAirBlock);
    }

    private static boolean isTopBlock(BlockPos pos, BlockPos originalPos, int radius, boolean halfCutted) {
        if (halfCutted) {
            return originalPos.getY() == pos.getY();
        }

        return roundDistance(pos, originalPos) == radius
                && roundDistance(pos.up(), originalPos) > radius;
    }

    private static boolean isBottomBlock(BlockPos pos, BlockPos originalPos, int radius) {
        return roundDistance(pos, originalPos) == radius
                && roundDistance(pos.down(), originalPos) > radius;
    }

    private static int roundDistance(BlockPos first, BlockPos second) {
        return (int) Math.round(Math.sqrt(first.distanceSq(second)));
    }

    private static int getRadius(Random rand, PlanetConfig config) {
        int radius = config.getMinRadius();
        int maxRadius = config.getMaxRadius();
        if (maxRadius > radius) {
            radius += rand.nextInt(maxRadius - radius);
        }

        return radius;
    }

    public static BlockPos chooseRandPos(Random rand, BlockPos position, int minY, int maxY, int radius) {
        int y = minY;
        int yMax = maxY;
        if (yMax > y) {
            y += rand.nextInt(yMax - y);
        }

        position = new ChunkPos(position).getBlock(16, y, 16);

        int freeZone = (30 - radius * 2) / 2;
        if (freeZone > 1) {
            position = position.add(rand.nextInt(freeZone) - freeZone, 0, rand.nextInt(freeZone) - freeZone);
        }

        return position;
    }

    private static boolean isHalfCutted(BlockPos pos, Random random) {
        int y = pos.getY();

        // lower path
        if (40 <= y && y <= 60) {
            return true;
        }

        // high path
        if (150 <= y && y <= 170) {
            return true;
        }

        return random.nextInt(40) == 0;
    }

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos chunkStart) {
        PlanetConfig config = configSupplier.apply(rand);
        if (config == null)
            return false;

        final int radius = getRadius(rand, config);
        final BlockPos position = chooseRandPos(rand, chunkStart, config.getyMin(), config.getyMax(worldIn), radius);

        if (checkIsFree && !checkIsFree(worldIn, position, radius))
            return false;

        final IBlockState bottomInstance = config.getBottom();
        final IBlockState inInstance = config.getIn();
        final IBlockState outInctsnce = config.getSide();
        final IBlockState topInstance = config.getTop();

        boolean halfCutted = config.isAlwaysHalf() || isHalfCutted(position, rand);
        final int yUpRadius = halfCutted ? 0 : radius;

        BlockPos.getAllInBoxMutable(position.add(-radius, -radius, -radius),
                position.add(radius, yUpRadius, radius))
                .forEach(currentPos -> setBlock(worldIn, position, currentPos, halfCutted, radius, topInstance, bottomInstance, outInctsnce, inInstance));

        return true;
    }

    private void setBlock(final World world, final BlockPos original, BlockPos current, boolean half, int radius, IBlockState top, IBlockState bottom, IBlockState side, IBlockState in) {
        int d = roundDistance(current, original);

        if (d == radius) {
            IBlockState currentState = isBottomBlock(current, original, radius)
                    ? bottom
                    : isTopBlock(current, original, radius, half)
                    ? top
                    : side;
            setBlockAndNotifyAdequately(world, current, currentState);

        } else if (d < radius) {
            IBlockState currentState = half && isTopBlock(current, original, radius, true)
                    ? top
                    : in;
            setBlockAndNotifyAdequately(world, current, currentState);
        }
    }
}
