package divineadditions.item;

import divineadditions.DivineAdditions;
import divineadditions.api.IRifleCore;
import divineadditions.capability.CapabilityItemProvider;
import divineadditions.gui.GuiHandler;
import divineadditions.gui.inventory.RifleInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;
import java.util.List;

public class ItemModRifle extends Item {

    public ItemModRifle() {
        setMaxDamage(824);
        setMaxStackSize(1);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new CapabilityItemProvider(new InvWrapper(new RifleInventory()));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {

        ItemStack itemStack = playerIn.getHeldItem(handIn);
        IItemHandler itemHandler = itemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

        if (itemHandler == null)
            return super.onItemRightClick(worldIn, playerIn, handIn);

        if (playerIn.isSneaking()) {
            BlockPos position = playerIn.getPosition();
            playerIn.openGui(DivineAdditions.instance, GuiHandler.RifleGuiId, worldIn, position.getX(), position.getY(), position.getZ());
            return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
        }

        if (itemHandler instanceof InvWrapper && ((InvWrapper) itemHandler).getInv() instanceof RifleInventory) {
            RifleInventory rifleInventory = (RifleInventory) ((InvWrapper) itemHandler).getInv();
            ItemStack core = rifleInventory.getCore();
            List<ItemStack> bullets = rifleInventory.getBullets();
            List<ItemStack> catalysts = rifleInventory.getCatalysts();

            if (!core.isEmpty() && !bullets.isEmpty()) {
                IRifleCore coreItem = (IRifleCore) core.getItem();

                ItemStack usingBullets = bullets.stream().filter(stack -> coreItem.acceptableForBullets(stack, false)).findFirst().orElse(ItemStack.EMPTY);
                if (!usingBullets.isEmpty()) {
                    ItemStack usingCatalyst = catalysts.stream().filter(stack -> coreItem.acceptableForCatalyst(stack, false)).findFirst().orElse(ItemStack.EMPTY);
                    if (!usingCatalyst.isEmpty()) {
                        coreItem.shoot(worldIn, playerIn, usingBullets, usingCatalyst);
                    }
                }
            }
        }

        return new ActionResult<>(EnumActionResult.FAIL, itemStack);
    }
}
