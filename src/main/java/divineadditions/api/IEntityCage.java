package divineadditions.api;

import divineadditions.DivineAdditions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface IEntityCage extends IEntityCatcher {
    static String entityIdName = "id";

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
