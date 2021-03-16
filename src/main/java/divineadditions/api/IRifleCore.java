package divineadditions.api;


import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IRifleCore {
    /**
     * Creates special bullet
     *
     * @param world   current world
     * @param thrower - thrower
     * @return
     */
    boolean shoot(World world, EntityLivingBase thrower, ItemStack bullets, ItemStack catalyst);

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
}
