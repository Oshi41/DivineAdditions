package divineadditions.item;

import divineadditions.DivineAdditions;
import divineadditions.api.IInstantRanged;
import divineadditions.msg.ParticleMessage;
import divineadditions.utils.WorldUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nonnull;

public class ItemInstantRangedWeapon extends ItemMod implements IInstantRanged {
    private static final RayTraceResult MISS = new RayTraceResult(RayTraceResult.Type.MISS, new Vec3d(0, 0, 0), EnumFacing.NORTH, BlockPos.ORIGIN);
    private final int maxRange;
    private ToolMaterial material;

    public ItemInstantRangedWeapon(int maxRange, ToolMaterial material) {
        this.maxRange = maxRange;
        this.material = material;

        setMaxDamage(material.getMaxUses());
        setMaxStackSize(1);

        tooltip.add(new TextComponentTranslation("divineadditions.tooltip.range_damage", maxRange));
    }

    @Nonnull
    @Override
    public RayTraceResult findTarget(World world, EntityLivingBase holder) {
        Entity nearest = WorldUtils.findNearest(holder, maxRange);
        if (nearest == null)
            return MISS;

        return new RayTraceResult(nearest);
    }

    @Override
    public int getItemEnchantability() {
        return material.getEnchantability();
    }

    /**
     * Return whether this item is repairable in an anvil.
     *
     * @param toRepair the {@code ItemStack} being repaired
     * @param repair   the {@code ItemStack} being used to perform the repair
     */
    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        ItemStack mat = this.material.getRepairItemStack();
        if (!mat.isEmpty() && net.minecraftforge.oredict.OreDictionary.itemMatches(mat, repair, false)) return true;
        return super.getIsRepairable(toRepair, repair);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return super.canApplyAtEnchantingTable(stack, enchantment)
                || enchantment == Enchantments.LOOTING;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        RayTraceResult target = findTarget(worldIn, playerIn);
        ItemStack itemStack = playerIn.getHeldItem(handIn);
        if (target.typeOfHit == RayTraceResult.Type.MISS) {
            return ActionResult.newResult(EnumActionResult.FAIL, itemStack);
        }

        EnumActionResult result = performHit(worldIn, playerIn, target, itemStack);

        if (result == EnumActionResult.SUCCESS) {
            if (!worldIn.isRemote) {
                itemStack.damageItem(1, playerIn);

                Vec3d start = playerIn.getPositionEyes(1);
                Vec3d lookVec = playerIn.getLook(1).normalize();
                float distance = target.entityHit.getDistance(playerIn);
                double range = 0.25;
                AxisAlignedBB bb = new AxisAlignedBB(-range, -range, -range, range, range, range);

                for (int i = 0; i < distance; i++) {
                    NetworkRegistry.TargetPoint targetPoint = new NetworkRegistry.TargetPoint(worldIn.provider.getDimension(),
                            start.x + (lookVec.x * i),
                            start.y + (lookVec.y * i),
                            start.z + (lookVec.z * i),
                            maxRange);

                    ParticleMessage message = new ParticleMessage(new Vec3d(targetPoint.x, targetPoint.y, targetPoint.z), EnumParticleTypes.CRIT, 1, bb);
                    DivineAdditions.networkWrapper.sendToAllTracking(message, targetPoint);
                }
            }
        }

        return ActionResult.newResult(result, itemStack);
    }

    /**
     * Performs actual hit of
     *
     * @param world
     * @param player
     * @param hit
     * @param itemStack
     * @return
     */
    protected EnumActionResult performHit(World world, EntityLivingBase player, RayTraceResult hit, ItemStack itemStack) {
        if (hit.typeOfHit != RayTraceResult.Type.ENTITY || hit.entityHit == null)
            return EnumActionResult.FAIL;

        if (!world.isRemote)
            hit.entityHit.attackEntityFrom(DamageSource.causeMobDamage(player), material.getAttackDamage());

        return EnumActionResult.SUCCESS;
    }
}
