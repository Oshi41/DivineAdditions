package divineadditions.entity;

import divineadditions.api.IEntityCage;
import divineadditions.api.IEntityCatcher;
import divineadditions.holders.Items;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityCageBullet extends EntityThrowable implements IEntityCatcher {

    public EntityCageBullet(World worldIn) {
        super(worldIn);
    }

    public EntityCageBullet(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result.typeOfHit == RayTraceResult.Type.MISS)
            return;

        if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
            setDead();
            return;
        }

        if (tryCatch(result.entityHit, (IEntityCage) Items.caged_mob)
                && getEntityWorld().isRemote) {
            spawnParticles(world, result.entityHit.getPosition());
        }

        setDead();
    }

    private void spawnParticles(World world, BlockPos position) {

    }
}
