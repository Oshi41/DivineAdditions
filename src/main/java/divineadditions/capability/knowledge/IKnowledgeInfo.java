package divineadditions.capability.knowledge;

import divineadditions.api.ICapabilitySync;
import divineadditions.utils.Property;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public interface IKnowledgeInfo extends ICapabilitySync {
    @CapabilityInject(IKnowledgeInfo.class)
    Capability<IKnowledgeInfo> KnowledgeCapability = null;

    /**
     * Level of old knowledges
     *
     * @return
     */
    Property<Integer> level();

    /**
     * Count of defender summones
     *
     * @return
     */
    Property<Integer> defender();
}
