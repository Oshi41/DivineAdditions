package divineadditions.api;

import divineadditions.DivineAdditions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public interface IEntityCatcher {
    static String cagedTagName = "cage";

    /**
     * Tryin to catch entity
     *
     * @param entity - entity to catch
     * @return
     */
    default boolean tryCatch(Entity entity, NBTTagCompound compound) {
        if (canCatch(entity) && compound != null) {
            NBTTagCompound entityTag = entity.serializeNBT();
            compound.setTag(cagedTagName, entityTag);
            return true;
        }

        return false;
    }

    default boolean canCatch(Entity entity) {
        if (entity != null) {
            if (entity.isNonBoss()) {
                if (entity.isEntityAlive()) {
                    if (EntityLivingBase.class.isAssignableFrom(entity.getClass())) {
                        return !(entity instanceof EntityPlayer);
                    }
                }
            }
        }

        return false;
    }
}
