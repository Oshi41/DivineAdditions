package divineadditions.proxy;

import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.server.FMLServerHandler;

import javax.annotation.Nonnull;

public class ServerProxy extends ProxyBase {
    @Override
    protected long getCurrentTick() {
        return FMLServerHandler.instance().getServer().getCurrentTime();
    }

    @Nonnull
    @Override
    protected IThreadListener getThreadListener() {
        return FMLServerHandler.instance().getServer();
    }

    @Override
    public boolean isDedicatedServer() {
        return true;
    }

    @SubscribeEvent
    protected void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            update();
        }
    }
}
