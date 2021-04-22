package divineadditions.utils;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class WorldUtils {

    public static void swapBlocks(World world, BlockPos pos, Function<IBlockState, IBlockState> mapperFunc) {
        if (world == null || pos == null || mapperFunc == null)
            return;

        IBlockState old = world.getBlockState(pos);
        TileEntity tileEntity = world.getTileEntity(pos);

        IBlockState state = mapperFunc.apply(old);

        world.setBlockState(pos, state, 3);
        //world.setBlockState(pos, state, 3);

        if (tileEntity != null) {
            tileEntity.validate();
            world.setTileEntity(pos, tileEntity);
        }
    }

    @Nullable
    public static Entity findNearest(EntityLivingBase viewEntity, double parDistance) {
        try {
            parDistance = getDistanceToClosestSolidWall(viewEntity, parDistance);
            Entity Return = null;
            double closest = parDistance;
            if (viewEntity != null) {
                World worldObj = viewEntity.getEntityWorld();
                RayTraceResult objectMouseOver = rayTrace(viewEntity, parDistance);
                Vec3d playerPosition = new Vec3d(viewEntity.posX, viewEntity.posY + 1.5D, viewEntity.posZ);
                if (objectMouseOver != null) {
                    parDistance = getDistanceToClosestSolidWall(viewEntity, parDistance);
                }

                Vec3d dirVec = viewEntity.getLookVec();
                Vec3d lookFarCoord = playerPosition.addVector(dirVec.x * parDistance, dirVec.y * parDistance, dirVec.z * parDistance);
                List<Entity> targettedEntities = worldObj.getEntitiesWithinAABBExcludingEntity(viewEntity, viewEntity.getEntityBoundingBox().expand(dirVec.x * parDistance, dirVec.y * parDistance, dirVec.z * parDistance));
                Iterator var12 = targettedEntities.iterator();

                while (var12.hasNext()) {
                    Entity targettedEntity = (Entity) var12.next();
                    if (targettedEntity != null && !targettedEntity.isInvisible()) {
                        double precheck = viewEntity.getDistance(targettedEntity);
                        RayTraceResult mopElIntercept = targettedEntity.getEntityBoundingBox().calculateIntercept(playerPosition, lookFarCoord);
                        if (mopElIntercept != null && precheck < closest) {
                            Return = targettedEntity;
                            closest = precheck;
                        }
                    }
                }
            }

            return Return;
        } catch (Throwable var17) {
            return null;
        }
    }

    public static RayTraceResult rayTrace(EntityLivingBase viewEntity, double p_70614_1_) {
        Vec3d vec3 = new Vec3d(viewEntity.posX, viewEntity.posY + 1.5D, viewEntity.posZ);
        Vec3d vec31 = viewEntity.getLookVec();
        Vec3d vec32 = vec3.addVector(vec31.x * p_70614_1_, vec31.y * p_70614_1_, vec31.z * p_70614_1_);
        return viewEntity.getEntityWorld().rayTraceBlocks(vec3, vec32, false, false, true);
    }

    public static double getDistanceToClosestSolidWall(EntityLivingBase viewEntity, double traceDistance) {
        return getClosestSolidWall(viewEntity, new Vec3d(viewEntity.posX, viewEntity.posY + 1.5D, viewEntity.posZ), traceDistance, 0, 0.0D);
    }

    public static double getClosestSolidWall(EntityLivingBase viewEntity, Vec3d startPosition, double traceDistance, int count, double offset) {
        if (count++ <= 20 && traceDistance - offset > 0.0D) {
            Vec3d vec31 = viewEntity.getLookVec();
            Vec3d vec32 = startPosition.addVector(vec31.x * (traceDistance - offset), vec31.y * (traceDistance - offset), vec31.z * (traceDistance - offset));
            RayTraceResult objectMouseOver = viewEntity.getEntityWorld().rayTraceBlocks(startPosition, vec32, false, false, true);
            if (objectMouseOver != null) {
                IBlockState bs = viewEntity.getEntityWorld().getBlockState(objectMouseOver.getBlockPos());
                Block block = bs.getBlock();
                if (bs.isOpaqueCube() && !block.isAir(bs, viewEntity.getEntityWorld(), objectMouseOver.getBlockPos())) {
                    return objectMouseOver.hitVec.distanceTo(new Vec3d(viewEntity.posX, viewEntity.posY + 1.5D, viewEntity.posZ));
                }

                return getClosestSolidWall(viewEntity, objectMouseOver.hitVec.addVector(vec31.x, vec31.y, vec31.z), traceDistance, count, objectMouseOver.hitVec.distanceTo(startPosition));
            }

        }
        return traceDistance;
    }
}
