package divineadditions.tile;

import divineadditions.api.IPedestal;
import divineadditions.tile.base.TileSyncBase;
import net.minecraftforge.items.IItemHandler;
import openmods.utils.InventoryUtils;

public class TileEntityStackHolder extends TileSyncBase implements IPedestal {
    public TileEntityStackHolder() {
    }

    @Override
    public IItemHandler getHandler() {
        return InventoryUtils.tryGetHandler(getWorld(), getPos(), null);
    }
}
