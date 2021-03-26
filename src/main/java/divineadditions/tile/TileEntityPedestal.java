package divineadditions.tile;

import divineadditions.api.IPedestal;
import divineadditions.tile.base.TileSyncBase;

/**
 * Own class is needed for render mappings
 */
public class TileEntityPedestal extends TileSyncBase implements IPedestal {
    @Override
    public int getStackSize() {
        return Integer.MAX_VALUE;
    }
}
