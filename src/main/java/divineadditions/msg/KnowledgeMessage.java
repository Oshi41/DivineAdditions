package divineadditions.msg;

import divineadditions.capability.knowledge.IKnowledgeInfo;
import net.minecraftforge.common.capabilities.Capability;

public class KnowledgeMessage extends PlayerCapabilityChangedMessageBase<IKnowledgeInfo> {

    public KnowledgeMessage() {
    }

    public KnowledgeMessage(IKnowledgeInfo info) {
        super(info);
    }

    @Override
    public Capability<IKnowledgeInfo> getCap() {
        return IKnowledgeInfo.KnowledgeCapability;
    }
}
