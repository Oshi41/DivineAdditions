package divineadditions.item.rifle_core;

import divineadditions.api.IRifleCoreConfig;
import divineadditions.config.DivineAdditionsConfig;
import divineadditions.entity.EntityCageBullet;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemRifleMobCore extends ItemRifleCoreBase {

    public ItemRifleMobCore() {
        super(EnumParticleTypes.SMOKE_NORMAL);
    }

    @Override
    public Entity createBulletEntity(World world, EntityLivingBase thrower, ItemStack core, ItemStack bullets, ItemStack catalyst) {
        EntityCageBullet bullet = new EntityCageBullet(world, thrower);
        Vec3d bulletPos = thrower.getPositionEyes(1).add(thrower.getLookVec());
        bullet.setPosition(bulletPos.x, bulletPos.y, bulletPos.z);
        bullet.shoot(thrower, thrower.rotationPitch, thrower.rotationYaw, 0, 3, 1);
        return bullet;
    }

    @Override
    public IRifleCoreConfig getCurrentConfig() {
        return DivineAdditionsConfig.rifleConfig.mobCoreConfig;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TextComponentTranslation("divineadditions.tooltip.rifle_mob_core").getFormattedText());
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
