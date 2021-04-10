package divineadditions.capability.knowledge;

import divineadditions.DivineAdditions;
import divineadditions.msg.KnowledgeMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

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
            KnowledgeMessage msg = new KnowledgeMessage(this);
            DivineAdditions.networkWrapper.sendTo(msg, ((EntityPlayerMP) player));
        }
    }
}
