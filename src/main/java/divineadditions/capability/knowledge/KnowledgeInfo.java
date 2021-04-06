package divineadditions.capability.knowledge;

import divineadditions.DivineAdditions;
import divineadditions.msg.PlayerCapabilityChangedMessageBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.capabilities.Capability;

public class KnowledgeInfo implements IKnowledgeInfo {
    private int level = 0;
    private int defender = 0;

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int lvl) {
        level = lvl;
    }

    @Override
    public int armorDefenderSummonCount() {
        return defender;
    }

    @Override
    public void setArmorDefenderSummonCount(int count) {
        defender = count;
    }

    @Override
    public void update(EntityPlayer player) {
        if (player instanceof EntityPlayerMP) {
            DivineAdditions.networkWrapper.sendTo(new PlayerCapabilityChangedMessageBase<IKnowledgeInfo>(this) {
                @Override
                public Capability<IKnowledgeInfo> getCap() {
                    return IKnowledgeInfo.KnowledgeCapability;
                }
            }, ((EntityPlayerMP) player));
        }
    }
}
