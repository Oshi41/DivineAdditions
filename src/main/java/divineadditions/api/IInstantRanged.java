package divineadditions.api;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public interface IInstantRanged {

    /**
     * Find nearest target. MISS if cant find any suitable target
     *
     * @param world  - current world
     * @param holder - current entity holding this weapon
     * @return
     */
    @Nonnull
    RayTraceResult findTarget(World world, EntityLivingBase holder);
}
