package divineadditions.capability.gravity;

import divineadditions.capability.gravity.source.base.IGravitySource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.BlockPos;

public class GravityUtils {

    public static boolean applyGravity(Entity entity, IGravitySource source) {
        if (entity == null || source == null)
            return false;

        if (source.getMultiplier() == 1)
            return true;

        Double currentGravity = getGravity(entity);
        if (currentGravity == null)
            return false;

        double gravityTick = currentGravity * source.getMultiplier();
        entity.addVelocity(0, currentGravity - gravityTick, 0);
        return true;
    }

    /**
     * Copy of entity gravity logic
     *
     * @param entity
     * @return
     */
    public static Double getGravity(Entity entity) {
        if (entity == null || entity.hasNoGravity())
            return null;

        if (entity instanceof EntityItem)
            return 0.04;

        // cant apply on flying player
        if (entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isFlying)
            return null;

        // already flying entity
        if (entity instanceof EntityFlying || entity instanceof net.minecraft.entity.passive.EntityFlying)
            return null;

        if (entity instanceof EntityLivingBase) {
            EntityLivingBase livingBase = (EntityLivingBase) entity;

            if (livingBase.isServerWorld() || livingBase.canPassengerSteer()) {
                if (entity.isInWater() || entity.isInLava()) {
                    return 0.02;
                }

                if (!livingBase.isElytraFlying()) {
                    if (!livingBase.isPotionActive(MobEffects.LEVITATION)) {
                        BlockPos pos = new BlockPos(entity.posX, 0.0D, entity.posZ);
                        if (!entity.world.isRemote
                                || entity.world.isBlockLoaded(pos)
                                && entity.world.getChunkFromBlockCoords(pos).isLoaded()) {
                            return 0.08;
                        }
                    }
                }
            }
        }

        return null;
    }
}
