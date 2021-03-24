package divineadditions.capability.gravity.source;

import divineadditions.capability.gravity.GravityUtils;
import divineadditions.capability.gravity.source.base.GravitySourceBase;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;

public class TileGravitySource extends GravitySourceBase<TileEntity> {
    private final int hashCode;
    private final int radius;
    private final double multiplier;

    public TileGravitySource(TileEntity owner, int radius, double multiplier) {
        super(owner);

        hashCode = owner.hashCode();
        this.radius = radius;
        this.multiplier = multiplier;
    }

    @Override
    public double getMultiplier() {
        return multiplier;
    }

    @Override
    public boolean applyGravity(ICapabilityProvider provider) {
        if (provider instanceof TileEntity) {
            for (Entity entity : ((TileEntity) provider).getWorld().getEntitiesWithinAABB(Entity.class, getSize())) {
                GravityUtils.applyGravity(entity, this);
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean canApplyTo(@Nonnull Entity e) {
        TileEntity tileEntity = getOwner();
        if (tileEntity == null)
            return false;

        AxisAlignedBB size = getSize();
        if (!size.intersects(e.getEntityBoundingBox())) {
            return false;
        }

        if (tileEntity.getWorld() != e.getEntityWorld())
            return false;

        return tileEntity.getWorld().getEntitiesWithinAABB(Entity.class, size).contains(e);
    }

    @Override
    public int hashCode() {
        TileEntity owner = getOwner();
        return owner == null ? 0 : owner.hashCode();
    }

    private AxisAlignedBB getSize() {
        BlockPos pos = getOwner().getPos();

        return new AxisAlignedBB(
                pos.add(-radius, -radius, -radius),
                pos.add(radius, radius, radius)
        );
    }
}
