package divineadditions.world.gen;

import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class WorldGenVines extends net.minecraft.world.gen.feature.WorldGenVines {
    @Override
    public boolean generate(World worldIn, Random rand, final BlockPos originalPosition) {
        BlockPos.MutableBlockPos position = new BlockPos.MutableBlockPos(originalPosition);

        for (; position.getY() < worldIn.getHeight(); position.setY(position.getY() + 1)) {
            if (worldIn.isAirBlock(position)) {
                for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL.facings()) {
                    if (Blocks.VINE.canPlaceBlockOnSide(worldIn, position, enumfacing)) {
                        enumfacing = enumfacing.getOpposite();
                        IBlockState iblockstate = Blocks.VINE.getDefaultState()
                                .withProperty(BlockVine.NORTH, enumfacing == EnumFacing.NORTH)
                                .withProperty(BlockVine.EAST, enumfacing == EnumFacing.EAST)
                                .withProperty(BlockVine.SOUTH, enumfacing == EnumFacing.SOUTH)
                                .withProperty(BlockVine.WEST, enumfacing == EnumFacing.WEST);
                        setBlockAndNotifyAdequately(worldIn, position, iblockstate);
                        break;
                    }
                }
            } else {
                position.setPos(
                        originalPosition.getX() + rand.nextInt(4) - rand.nextInt(4),
                        position.getY(),
                        originalPosition.getZ() + rand.nextInt(4) - rand.nextInt(4));
            }
        }

        return true;
    }
}
