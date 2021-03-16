package divineadditions.api;

import net.minecraft.entity.Entity;
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
}
