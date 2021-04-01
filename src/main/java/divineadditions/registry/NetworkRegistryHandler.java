package divineadditions.registry;

import divineadditions.DivineAdditions;
import divineadditions.msg.KnowledgeMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkRegistryHandler {
    private static int i = 0;

    public static void register() {
        registerForSide(KnowledgeMessage.class, Side.CLIENT);
        registerForSide(KnowledgeMessage.class, Side.SERVER);
    }

    private static <T extends IMessage> void registerForSide(Class<T> klass, Side side) {
        DivineAdditions.networkWrapper.registerMessage((message, ctx) -> DivineAdditions.proxy.onMessage(message, ctx), klass, i++, side);
    }
}
