package divineadditions.proxy;

import divineadditions.api.IProxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class ServerProxy implements IProxy {
    @Override
    public boolean isDedicatedServer() {
        return true;
    }

    @Override
    public void pre() {

    }

    @Override
    public void init() {

    }

    @Override
    public void post() {

    }
}
