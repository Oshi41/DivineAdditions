package divineadditions.entity;

import divineadditions.api.EntityDamageSourceIndirectEnch;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.Map;

public class EntityBullet extends EntityThrowable {
    private ItemStack heldItem = ItemStack.EMPTY;
    private int damage = 0;

    public EntityBullet(World worldIn) {
        super(worldIn);
    }

    public EntityBullet(World worldIn, EntityLivingBase throwerIn, int damage, Map<Enchantment, Integer> enchantments) {
        super(worldIn, throwerIn);
        this.damage = damage;

        heldItem = new ItemStack(Blocks.STONE);
        EnchantmentHelper.setEnchantments(enchantments, heldItem);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        damage = compound.getInteger("DamageAmount");
        compound.setTag("Stack", heldItem.writeToNBT(new NBTTagCompound()));
        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound nbt = super.writeToNBT(compound);
        nbt.setInteger("DamageAmount", damage);
        heldItem = new ItemStack(nbt.getCompoundTag("Stack"));
        return nbt;
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        switch (result.typeOfHit) {
            case MISS:
                return;

            case BLOCK:
                setDead();
                return;

            case ENTITY:

                if (result.entityHit != null) {
                    attackEntityWith(result.entityHit, heldItem);
                }

                setDead();
                return;
        }
    }

    private void attackEntityWith(Entity entity, ItemStack heldItem) {
        float damageAmount = damage;

        if (entity instanceof EntityLivingBase) {
            damageAmount += EnchantmentHelper.getModifierForCreature(heldItem, ((EntityLivingBase) entity).getCreatureAttribute());
        }

        entity.attackEntityFrom(EntityDamageSourceIndirectEnch.createThrowable(this, EnchantmentHelper.getEnchantments(heldItem)), damageAmount);
    }
}
