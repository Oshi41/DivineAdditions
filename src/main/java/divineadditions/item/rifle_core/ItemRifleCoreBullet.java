package divineadditions.item.rifle_core;

import divineadditions.api.IRifleCoreConfig;
import divineadditions.config.DivineAdditionsConfig;
import divineadditions.entity.EntityBullet;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class ItemRifleCoreBullet extends ItemRifleCoreBase {

    public ItemRifleCoreBullet() {
        super(EnumParticleTypes.SMOKE_LARGE);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return super.canApplyAtEnchantingTable(stack, enchantment) ||
                enchantment == Enchantments.LOOTING ||
                enchantment == Enchantments.SHARPNESS ||
                enchantment == Enchantments.INFINITY;
    }

    @Override
    public Entity createBulletEntity(World world, EntityLivingBase thrower, ItemStack core, ItemStack bullets, ItemStack catalyst) {
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(core);

        EntityBullet bullet = new EntityBullet(world, thrower, DivineAdditionsConfig.rifleConfig.bulletCoreConfig.bulletDamage, enchantments);
        Vec3d bulletPos = thrower.getPositionEyes(1).add(thrower.getLookVec());
        bullet.setPosition(bulletPos.x, bulletPos.y, bulletPos.z);
        bullet.shoot(thrower, thrower.rotationPitch, thrower.rotationYaw, 0, 3, 1);

        return bullet;
    }

    @Nonnull
    @Override
    public IRifleCoreConfig getCurrentConfig() {
        return DivineAdditionsConfig.rifleConfig.bulletCoreConfig;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TextComponentTranslation("divineadditions.tooltip.rifle_bullet_core").getFormattedText());
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
