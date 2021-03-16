package divineadditions.api;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface IEntityCatcher {
    /**
     * Detect if can catch entity
     *
     * @param entity
     * @return
     */
    default boolean canCatch(Entity entity) {
        if (entity == null || !entity.isNonBoss() || !entity.isEntityAlive())
            return false;

        if (entity instanceof EntityPlayer)
            return false;

        if (getClass().isAssignableFrom(entity.getClass())) {
            return false;
        }

        if (this == entity) {
            return false;
        }

        return EntityLivingBase.class.isAssignableFrom(entity.getClass());
    }

    /**
     * Perform catching entity
     *
     * @param entity - entity to catch
     * @param item   - entity cage
     * @return
     */
    default boolean tryCatch(Entity entity, IEntityCage item) {
        if (!canCatch(entity) || !(item instanceof Item))
            return false;

        ItemStack itemStack = ((Item) item).getDefaultInstance();
        itemStack.setTagCompound(new NBTTagCompound());

        if (!item.imprison(entity, itemStack.getTagCompound())) {
            return false;
        }

        if (!entity.getEntityWorld().isRemote) {
            Block.spawnAsEntity(entity.getEntityWorld(), entity.getPosition(), itemStack);
            entity.setDead();
        }

        return true;
    }
}
