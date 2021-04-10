package divineadditions.msg;

import divineadditions.capability.knowledge.IKnowledgeInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;

public class KnowledgeMessage extends CapChangedMessageBase<IKnowledgeInfo> {

    public KnowledgeMessage() {
    }

    public KnowledgeMessage(IKnowledgeInfo info) {
        super(info);
    }

    @Override
    public ICapabilityProvider getFromPlayer(@Nonnull EntityPlayer player) {
        return player;
    }

    @Override
    public Capability<IKnowledgeInfo> getCap() {
        return IKnowledgeInfo.KnowledgeCapability;
    }
}
