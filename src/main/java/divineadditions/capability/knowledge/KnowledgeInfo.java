package divineadditions.capability.knowledge;

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
}
