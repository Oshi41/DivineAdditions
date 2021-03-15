package divineadditions.proxy;

import divineadditions.DivineAdditions;
import divineadditions.api.IProxy;
import divineadditions.debug.LangHelper;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy implements IProxy {

    @Override
    public boolean isDedicatedServer() {
        return false;
    }

    @Override
    public void pre() {
        OBJLoader.INSTANCE.addDomain(DivineAdditions.MOD_ID);
    }

    @Override
    public void init() {
    }

    @Override
    public void post() {
        new LangHelper(DivineAdditions.MOD_ID).fill();
    }
}
