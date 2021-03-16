package divineadditions.api;

import divineadditions.DivineAdditions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface IEntityCage {
    String entityIdName = "id";
    String cagedTagName = "cage";

    /**
     * Spawning containing entity
     *
     * @param world - current world
     * @param pos   - current pos
     * @return
     */
    default boolean release(World world, BlockPos pos, NBTTagCompound compound) {
        if (!DivineAdditions.proxy.isDedicatedServer())
            return false;

        ResourceLocation entityId = getContainingEntityId(compound);
        // entity id exists and has valid value
        if (entityId != null && EntityList.getClass(entityId) != null) {
            NBTTagCompound entityNbt = compound.getCompoundTag(cagedTagName);
            Entity entity = EntityList.createEntityFromNBT(entityNbt, world);
            if (entity != null) {
                // right position
                entity.setPosition(pos.getX(), pos.getY(), pos.getZ());
                boolean wasSpawned = world.spawnEntity(entity);

                if (wasSpawned) {
                    // remove entity from nbt tag
                    compound.setTag(cagedTagName, new NBTTagCompound());
                }

                return wasSpawned;
            }
        }

        return false;
    }

    /**
     * Put current entity to cage
     *
     * @param entity   - entity to store. You need to despawn it by yourself!
     * @param compound - NBT tag
     * @return
     */
    default boolean imprison(Entity entity, NBTTagCompound compound) {
        if (entity == null || compound == null) {
            return false;
        }

        NBTTagCompound entityTag = entity.serializeNBT();
        compound.setTag(cagedTagName, entityTag);
        return true;
    }

    @Nullable
    default ResourceLocation getContainingEntityId(NBTTagCompound compound) {
        if (compound != null) {
            if (compound.hasKey(cagedTagName)) {
                NBTTagCompound compoundTag = compound.getCompoundTag(cagedTagName);
                if (compoundTag.hasKey(entityIdName)) {
                    return new ResourceLocation(compoundTag.getString(entityIdName));
                }
            }
        }

        return null;
    }
}
