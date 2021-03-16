package divineadditions.item;

import divineadditions.DivineAdditions;
import divineadditions.api.IItemEntityBullet;
import divineadditions.config.DivineAdditionsConfig;
import divineadditions.gui.GuiHandler;
import divineadditions.gui.conainter.RifleContainer;
import divineadditions.gui.inventory.RifleInventory;
import divineadditions.utils.ItemStackHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import java.util.List;

public class ItemModRifle extends Item {

    public ItemModRifle() {
        setMaxDamage(DivineAdditionsConfig.rifleSettings.rifleDurability);
        setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack heldItem = playerIn.getHeldItem(handIn);

        IItemHandler itemHandler = heldItem.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (itemHandler != null) {
            if (playerIn.isSneaking()) {
                BlockPos position = playerIn.getPosition();

                playerIn.openGui(DivineAdditions.instance, GuiHandler.RifleGuiId, worldIn, position.getX(), position.getY(), position.getZ());

                return new ActionResult<>(EnumActionResult.SUCCESS, heldItem);
            } else {
                if (itemHandler instanceof InvWrapper && ((InvWrapper) itemHandler).getInv() instanceof RifleInventory) {
                    RifleInventory container = (RifleInventory) ((InvWrapper) itemHandler).getInv();
                    List<ItemStack> bullets = container.getBullets();
                    if (!bullets.isEmpty()) {
                        ItemStack bullet = bullets.get(0);
                        IItemEntityBullet bulletItem = (IItemEntityBullet) bullet.getItem();

                        ItemStack neededCatalyst = bulletItem.getCatalyst();

                        for (ItemStack catalyst : container.getCatalysts()) {
                            if (bulletItem.tryConsume(playerIn, catalyst, neededCatalyst)) {
                                ItemStackHelper.shrink(bullet, playerIn, 1);

                                Entity entity = bulletItem.createBulletEntity(worldIn, playerIn);
                                if (entity != null) {

                                    if (!worldIn.isRemote) {
                                        worldIn.spawnEntity(entity);
                                    }

                                    playerIn.getCooldownTracker().setCooldown(heldItem.getItem(), DivineAdditionsConfig.rifleSettings.rifleCooldown);

                                    heldItem.damageItem(1, playerIn);

                                    if (worldIn.isRemote) {
                                        particleExplosion(worldIn, playerIn);
                                    }

                                    return new ActionResult<>(EnumActionResult.SUCCESS, heldItem);
                                }
                            }
                        }
                    }
                }
            }
        }

        return ActionResult.newResult(EnumActionResult.FAIL, heldItem);
    }

    private void particleExplosion(World world, EntityLivingBase entity) {
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
}
