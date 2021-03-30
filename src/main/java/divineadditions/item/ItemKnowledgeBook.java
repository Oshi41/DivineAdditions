package divineadditions.item;

import divineadditions.DivineAdditions;
import divineadditions.api.IForgeInventory;
import divineadditions.utils.NbtUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class ItemKnowledgeBook extends ItemBook {
    private final int level;

    public ItemKnowledgeBook(int level) {
        this.level = level;

        setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        NBTTagCompound tag = NbtUtils.getOrCreateModPlayerPersistTag(playerIn, DivineAdditions.MOD_ID);
        if (!tag.hasKey(IForgeInventory.ForgeLevelName) || tag.getInteger(IForgeInventory.ForgeLevelName) < level) {
            tag.setInteger(IForgeInventory.ForgeLevelName, level);
            ItemStack heldItem = playerIn.getHeldItem(handIn);
            heldItem.shrink(1);
            if (!playerIn.getEntityWorld().isRemote) {
                playerIn.sendMessage(new TextComponentTranslation("divineadditions.message.current_knowledge_level_changed", level));
            }
            playerIn.setHeldItem(handIn, heldItem);
            return new ActionResult<>(EnumActionResult.SUCCESS, heldItem);
        }


        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
