package divineadditions.item;

import divineadditions.capability.knowledge.IKnowledgeInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
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

        IKnowledgeInfo capability = playerIn.getCapability(IKnowledgeInfo.KnowledgeCapability, null);
        if (capability != null && capability.getLevel() < level) {
            capability.setLevel(level);

            ItemStack heldItem = playerIn.getHeldItem(handIn);
            heldItem.shrink(1);

            if (!playerIn.getEntityWorld().isRemote) {
                playerIn.sendMessage(new TextComponentTranslation("divineadditions.message.current_knowledge_level_changed", level));
            }
        }

        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
