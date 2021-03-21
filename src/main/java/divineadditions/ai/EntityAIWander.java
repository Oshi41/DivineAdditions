package divineadditions.ai;

import net.minecraft.entity.EntityCreature;

public class EntityAIWander extends net.minecraft.entity.ai.EntityAIWander {
    private double speedIn;

    public EntityAIWander(EntityCreature creatureIn, double speedIn) {
        super(creatureIn, speedIn);
        this.speedIn = speedIn;
    }

    @Override
    public void startExecuting() {
        entity.getNavigator().tryMoveToXYZ(x, y, z, getSpeedIn());
    }

    public double getSpeedIn() {
        return speedIn;
    }

    public void setSpeedIn(double speedIn) {
        this.speedIn = speedIn;
    }
}
