package divineadditions.capability.knowledge;

import divineadditions.api.ICapabilitySync;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public interface IKnowledgeInfo extends ICapabilitySync {
    @CapabilityInject(IKnowledgeInfo.class)
    Capability<IKnowledgeInfo> KnowledgeCapability = null;

    /**
     * Returns current knowledge level
     *
     * @return
     */
    int getLevel();

    /**
     * Settings current knowledge level
     *
     * @param lvl
     */
    void setLevel(int lvl);

    /**
     * Gets current amount of cummoned armor defenders
     *
     * @return
     */
    int armorDefenderSummonCount();

    /**
     * Sets current armor defender summon count
     *
     * @param count
     */
    void setArmorDefenderSummonCount(int count);
}
