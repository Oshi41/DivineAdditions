package divineadditions.world.dimension.planet;

import divineadditions.config.PlanetConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.StreamSupport;

public class PlanetWorldGen extends WorldGenerator {
    private Function<Random, PlanetConfig> configSupplier;
    private int radius;
    private boolean checkIsFree;

    public PlanetWorldGen(Function<Random, PlanetConfig> config, int radius, boolean checkIsFree) {
        this.configSupplier = config;
        this.radius = radius;
        this.checkIsFree = checkIsFree;
    }

    private static boolean checkIsFree(World worldIn, BlockPos position, int radius) {
        return StreamSupport.stream(BlockPos.getAllInBoxMutable(position.add(-radius, -radius, -radius),
                position.add(radius, radius, radius)).spliterator(), false)
                .allMatch(worldIn::isAirBlock);
    }

    private static boolean isTopBlock(BlockPos pos, BlockPos originalPos, int radius) {
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

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        PlanetConfig config = configSupplier.apply(rand);
        if (config == null)
            return false;

        if (checkIsFree && !checkIsFree(worldIn, position, radius))
            return false;

        IBlockState bottomInstance = config.getBottom();
        IBlockState inInstance = config.getIn();
        IBlockState outInctsnce = config.getOut();
        IBlockState topInstance = config.getTop();

        BlockPos.getAllInBoxMutable(position.add(-radius, -radius, -radius),
                position.add(radius, radius, radius)).forEach(currentPos -> {

            int d = roundDistance(currentPos, position);

            if (d == radius) {
                if (isBottomBlock(currentPos, position, radius)) {
                    setBlockAndNotifyAdequately(worldIn, currentPos, bottomInstance);
                } else if (isTopBlock(currentPos, position, radius)) {
                    setBlockAndNotifyAdequately(worldIn, currentPos, topInstance);
                } else {
                    setBlockAndNotifyAdequately(worldIn, currentPos, outInctsnce);
                }
            } else if (d < radius) {
                setBlockAndNotifyAdequately(worldIn, currentPos, inInstance);
            }
        });

        return true;
    }
}
