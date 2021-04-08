package divineadditions.api;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import javax.annotation.Nullable;

/**
 * Continer implementing that interface will sync after gui opened
 */
public interface IContainerSync {

    /**
     * Crates sync message to send
     *
     * @return
     */
    @Nullable
    IMessage createMessage();
}
