package divineadditions.item.rifle_core;

import divineadditions.api.IRifleCore;
import divineadditions.api.IRifleCoreConfig;
import divineadditions.entity.EntityCageBullet;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemRifleMobCore extends Item implements IRifleCore {
    private final IRifleCoreConfig config;

    public ItemRifleMobCore(IRifleCoreConfig config) {
        this.config = config;
        setMaxStackSize(1);
        setMaxDamage(config.getDurability());
    }

    @Override
    public boolean shoot(World world, EntityLivingBase thrower, ItemStack bullets, ItemStack catalyst) {
        if (world != null && thrower != null && bullets != null && !bullets.isEmpty() && catalyst != null) {

            boolean isCreative = thrower instanceof EntityPlayer && ((EntityPlayer) thrower).isCreative();
            Integer bulletsAmount = config.getBullets().get(bullets.getItem().getRegistryName().toString());
            if (bulletsAmount != null && bullets.getCount() >= bulletsAmount) {
                Integer catalystAmount = config.getCatalysts().get(catalyst.getItem().getRegistryName().toString());
                if (catalystAmount != null && catalyst.getCount() >= catalystAmount) {

                    EntityCageBullet bullet = new EntityCageBullet(world, thrower);
                    Vec3d bulletPos = thrower.getPositionEyes(1).add(thrower.getLookVec());
                    bullet.setPosition(bulletPos.x, bulletPos.y, bulletPos.z);
                    bullet.shoot(thrower, thrower.rotationPitch, thrower.rotationYaw, 0, 3, 1);

                    if (!world.isRemote) {
                        if (world.spawnEntity(bullet) && !isCreative) {
                            bullets.shrink(bulletsAmount);
                            catalyst.shrink(catalystAmount);
                        }
                    } else {
                        spawnParticle(world, thrower);
                    }

                    if (thrower instanceof EntityPlayer) {
                        ((EntityPlayer) thrower).getCooldownTracker().setCooldown(thrower.getHeldItemMainhand().getItem(), getCooldown());
                    }

                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void spawnParticle(World world, EntityLivingBase entity) {
        Vec3d position = entity.getPositionEyes(1).add(entity.getLookVec());

        for (int i = 0; i < 20; i++) {
            Vec3d scale = entity.getLookVec().scale(world.rand.nextFloat());

            world.spawnParticle(
                    EnumParticleTypes.SMOKE_NORMAL,
                    position.x + world.rand.nextFloat() - world.rand.nextFloat(),
                    position.y + world.rand.nextFloat() - world.rand.nextFloat(),
                    position.z + world.rand.nextFloat() - world.rand.nextFloat(),
                    scale.x,
                    scale.y,
                    scale.z
            );
        }
    }

    @Override
    public IRifleCoreConfig getCurrentConfig() {
        return config;
    }
}
