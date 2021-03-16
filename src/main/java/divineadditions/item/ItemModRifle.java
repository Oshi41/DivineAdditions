package divineadditions.item;

import divineadditions.DivineAdditions;
import divineadditions.config.DivineAdditionsConfig;
import divineadditions.gui.GuiHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

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
                ItemStack bullets = itemHandler.getStackInSlot(0);
                ItemStack catalyst = itemHandler.getStackInSlot(1);

                if (!worldIn.isRemote && !bullets.isEmpty()) {
                    if (bullets.getItem() instanceof ItemEntityBullet) {
                        ItemEntityBullet bullet = (ItemEntityBullet) bullets.getItem();
                        if (tryConsume(catalyst, bullet.getCatalyst())) {
                            bullets.shrink(1);

                            Entity entity = bullet.createBulletEntity(worldIn, playerIn);

                            if (entity != null) {
                                worldIn.spawnEntity(entity);

                                playerIn.getCooldownTracker().setCooldown(heldItem.getItem(), DivineAdditionsConfig.rifleSettings.rifleCooldown);

                                heldItem.damageItem(1, playerIn);
                                return new ActionResult<>(EnumActionResult.SUCCESS, heldItem);
                            }
                        }
                    }
                }
            }
        }

        return ActionResult.newResult(EnumActionResult.FAIL, heldItem);
    }

    /**
     * trying to consume catalyst from slot
     *
     * @param source   - current slot
     * @param catalyst - needed catalyst
     * @return
     */
    public static boolean tryConsume(ItemStack source, ItemStack catalyst) {
        if (source.getItem() == catalyst.getItem()) {
            if (ItemStack.areItemStackShareTagsEqual(source, catalyst)) {
                if (source.getCount() >= catalyst.getCount()) {
                    source.shrink(catalyst.getCount());
                    return true;
                }
            }
        }

        return false;
    }
}
