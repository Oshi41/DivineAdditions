package divineadditions.item.rifle_core;

import divineadditions.api.IRifleCore;
import divineadditions.utils.ItemStackHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class ItemRifleCoreBase extends Item implements IRifleCore {
    private final EnumParticleTypes type;

    protected ItemRifleCoreBase(EnumParticleTypes type) {
        this.type = type;

        setMaxDamage(getCurrentConfig().getDurability());
        setMaxStackSize(1);
    }

    @Override
    public boolean shoot(World world, EntityLivingBase thrower, ItemStack core, ItemStack bullets, ItemStack catalyst) {
        if (world != null && thrower != null && bullets != null && catalyst != null) {

            boolean isCreative = thrower instanceof EntityPlayer && ((EntityPlayer) thrower).isCreative();
            Integer bulletsAmount = getCurrentConfig().getBullets().get(bullets.getItem().getRegistryName().toString());
            if (isCreative || bulletsAmount != null && bullets.getCount() >= bulletsAmount) {
                Integer catalystAmount = getCurrentConfig().getCatalysts().get(catalyst.getItem().getRegistryName().toString());
                if (isCreative || catalystAmount != null && catalyst.getCount() >= catalystAmount) {

                    Entity bullet = createBulletEntity(world, thrower, core, bullets, catalyst);

                    if (!world.isRemote) {
                        if (bullet != null)
                            world.spawnEntity(bullet);

                        if (!isCreative) {

                            if (EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, core) == 0) {
                                bullets.shrink(bulletsAmount);
                                core.damageItem(1, thrower);
                            }

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
    public boolean acceptableForBullets(ItemStack stack, boolean ignoreAmount) {
        Integer amount = getCurrentConfig().getBullets().get(stack.getItem().getRegistryName().toString());
        if (amount != null) {
            return ignoreAmount || stack.getCount() >= amount;
        }

        return false;
    }

    @Override
    public boolean acceptableForCatalyst(ItemStack stack, boolean ignoreAmount) {
        Integer amount = getCurrentConfig().getCatalysts().get(stack.getItem().getRegistryName().toString());
        if (amount != null) {
            return ignoreAmount || stack.getCount() >= amount;
        }

        return false;
    }

    @Override
    public int getCooldown() {
        return getCurrentConfig().getCoolddown();
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        String repairItemName = repair.getItem().getRegistryName().toString();
        return Arrays.stream(getCurrentConfig().getRepairItems()).anyMatch(x -> Objects.equals(repairItemName, x));
    }

    @Override
    public void spawnParticle(World world, EntityLivingBase thrower) {
        Vec3d position = thrower.getPositionEyes(1).add(thrower.getLookVec());

        for (int i = 0; i < 20; i++) {
            Vec3d scale = thrower.getLookVec().scale(world.rand.nextFloat());

            world.spawnParticle(
                    type,
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
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

        if (net.minecraft.client.gui.GuiScreen.isShiftKeyDown()) {
            List<ITextComponent> stacksInfo = ItemStackHelper.printStacks(getCurrentConfig().getBullets());
            if (!stacksInfo.isEmpty()) {
                stacksInfo.add(0, new TextComponentTranslation("divineadditions.tooltip.rifle.using_bullets"));
                stacksInfo.add(0, new TextComponentString(""));
            }

            List<ITextComponent> otherStacks = ItemStackHelper.printStacks(getCurrentConfig().getCatalysts());
            if (!otherStacks.isEmpty()) {
                stacksInfo.add(new TextComponentString(""));
                stacksInfo.add(new TextComponentTranslation("divineadditions.tooltip.rifle.using_catalysts"));

                stacksInfo.addAll(otherStacks);
            }

            otherStacks = ItemStackHelper.printStacks(getCurrentConfig().getRepairItems());
            if (!otherStacks.isEmpty()) {
                stacksInfo.add(new TextComponentString(""));
                stacksInfo.add(new TextComponentTranslation("divineadditions.tooltip.rifle.repair"));

                stacksInfo.addAll(otherStacks);
            }

            if (!stacksInfo.isEmpty()) {
                stacksInfo.add(new TextComponentString(""));
            }

            stacksInfo.forEach(x -> tooltip.add(x.getFormattedText()));
        }
    }
}
