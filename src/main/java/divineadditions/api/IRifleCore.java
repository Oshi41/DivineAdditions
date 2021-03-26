package divineadditions.api;


import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

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
    default boolean acceptableForCatalyst(ItemStack stack, boolean ignoreAmount) {
        Integer amount = getCurrentConfig().getCatalysts().get(stack.getItem().getRegistryName().toString());
        if (amount != null) {
            return ignoreAmount || stack.getCount() >= amount;
        }

        return false;
    }

    /**
     * Can accept current bullets
     *
     * @param stack - bullets stack
     * @return
     */
    default boolean acceptableForBullets(ItemStack stack, boolean ignoreAmount) {
        Integer amount = getCurrentConfig().getBullets().get(stack.getItem().getRegistryName().toString());
        if (amount != null) {
            return ignoreAmount || stack.getCount() >= amount;
        }

        return false;
    }

    /**
     * Gets cooldown for current core
     *
     * @return
     */
    default int getCooldown() {
        return getCurrentConfig().getCoolddown();
    }

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
