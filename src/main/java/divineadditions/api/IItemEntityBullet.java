package divineadditions.api;

import divineadditions.utils.ItemStackHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IItemEntityBullet extends IEntityCage {
    /**
     * Creates a current bullet entity
     *
     * @param world  - current world
     * @param player - thrower
     * @return
     */
    Entity createBulletEntity(World world, EntityPlayer player);

    /**
     * Gets catalyst needed for shooting a single bullet
     *
     * @return
     */
    ItemStack getCatalyst();

    /**
     * trying to consume catalyst from slot
     *
     * @param source   - current slot
     * @param catalyst - needed catalyst
     * @return
     */
    default boolean tryConsume(EntityLivingBase player, ItemStack source, ItemStack catalyst) {
        if (source.getItem() == catalyst.getItem()) {
            if (ItemStack.areItemStackShareTagsEqual(source, catalyst)) {
                if (source.getCount() >= catalyst.getCount()) {
                    ItemStackHelper.shrink(source, player, catalyst.getCount());
                    return true;
                }
            }
        }

        return false;
    }
}
