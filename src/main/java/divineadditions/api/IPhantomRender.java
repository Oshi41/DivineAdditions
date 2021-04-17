package divineadditions.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

import java.util.Map;

public interface IPhantomRender {

    /**
     * Returns phantom blocks we need to render
     *
     * @return
     */
    Map<BlockPos, IBlockState> getPhantomBlocks();
}
