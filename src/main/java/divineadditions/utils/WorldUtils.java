package divineadditions.utils;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Function;

public class WorldUtils {

    public static void swapBlocks(World world, BlockPos pos, Function<IBlockState, IBlockState> mapperFunc) {
        if (world == null || pos == null || mapperFunc == null)
            return;

        IBlockState old = world.getBlockState(pos);
        TileEntity tileEntity = world.getTileEntity(pos);

        IBlockState state = mapperFunc.apply(old);

        world.setBlockState(pos, state, 3);
        //world.setBlockState(pos, state, 3);

        if (tileEntity != null) {
            tileEntity.validate();
            world.setTileEntity(pos, tileEntity);
        }
    }
}
