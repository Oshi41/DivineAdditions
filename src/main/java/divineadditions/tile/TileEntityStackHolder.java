package divineadditions.tile;

import divineadditions.api.IPedestal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.items.IItemHandler;
import openmods.tileentity.SimpleNetTileEntity;
import openmods.utils.InventoryUtils;

public class TileEntityStackHolder extends SimpleNetTileEntity implements IPedestal {
    public TileEntityStackHolder() {

    }

    @Override
    public void openGui(Object instance, EntityPlayer player) {
        // no GUI
    }

    @Override
    public IItemHandler getHandler() {
        return InventoryUtils.tryGetHandler(getWorld(), getPos(), null);
    }
}
