package divineadditions.api;


import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public interface IRifleCore {
    /**
     * Launches special bullet for core
     *
     * @param world   current world
     * @param thrower - thrower
     * @param core
     * @return
     */
    boolean shoot(World world, EntityLivingBase thrower, ItemStack core, ItemStack bullets, ItemStack catalyst);

    /**
     * Creating special entity bullet
     *
     * @param world    - current world
     * @param thrower  - entity who made shot
     * @param core     - current core
     * @param bullets  - bullets stqack
     * @param catalyst - shoot catalyst
     * @return
     */
    Entity createBulletEntity(World world, EntityLivingBase thrower, ItemStack core, ItemStack bullets, ItemStack catalyst);

    /**
     * Checks wherever current stack is acceptble for catalyst
     *
     * @param stack
     * @return
     */
    boolean acceptableForCatalyst(ItemStack stack, boolean ignoreAmount);

    /**
     * Can accept current bullets
     *
     * @param stack - bullets stack
     * @return
     */
    boolean acceptableForBullets(ItemStack stack, boolean ignoreAmount);

    /**
     * Gets cooldown for current core
     *
     * @return
     */
    int getCooldown();

    @SideOnly(Side.CLIENT)
    void spawnParticle(World world, EntityLivingBase thrower);

    /**
     * Returns current config for core
     *
     * @return
     */
    @Nonnull
    IRifleCoreConfig getCurrentConfig();
}
