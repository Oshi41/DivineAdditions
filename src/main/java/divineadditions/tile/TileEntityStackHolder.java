package divineadditions.tile;

import divineadditions.api.IPedestal;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.IItemHandler;
import openmods.utils.InventoryUtils;

import javax.annotation.Nullable;

public class TileEntityStackHolder extends TileEntity implements IPedestal {
    public TileEntityStackHolder() {

    }

    @Override
    public IItemHandler getHandler() {
        return InventoryUtils.tryGetHandler(getWorld(), getPos(), null);
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, getBlockMetadata(), getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }
}
