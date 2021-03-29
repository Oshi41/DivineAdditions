package divineadditions.tile;

import divineadditions.api.IItemCapacity;
import divineadditions.tile.base.TileSyncBase;

/**
 * Own class is needed for render mappings
 */
public class TileEntityCatalystStand extends TileSyncBase implements IItemCapacity {
    @Override
    public int getStackSize() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getSlotCount() {
        return 3;
    }
}
