package divineadditions.item;

import divineadditions.capability.knowledge.IKnowledgeInfo;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ItemKnowledgeBook extends ItemBook {
    private final static String guidName = "Id";
    private final static String entityName = "Name";
    private final int level;

    public ItemKnowledgeBook(int level) {
        this.level = level;

        setMaxStackSize(1);
    }

    public static ItemStack createForPlayer(Item book, @Nonnull Entity entity) {
        ItemStack stack = new ItemStack(book);

        stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setUniqueId(guidName, entity.getUniqueID());
        stack.getTagCompound().setString(entityName, entity.getName());
        return stack;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemStack = playerIn.getHeldItem(handIn);
        NBTTagCompound compound = itemStack.getTagCompound();
        if (compound != null) {
            UUID id = compound.getUniqueId(guidName);
            if (id != playerIn.getUniqueID()) {
                if (!playerIn.getEntityWorld().isRemote) {
                    playerIn.sendMessage(new TextComponentTranslation("divineadditions.message.knowledge.someone_else_book"));
                }
            } else {
                IKnowledgeInfo capability = playerIn.getCapability(IKnowledgeInfo.KnowledgeCapability, null);
                if (capability != null && capability.getLevel() < level) {
                    capability.setLevel(level);

                    ItemStack heldItem = itemStack;
                    heldItem.shrink(1);

                    if (!playerIn.getEntityWorld().isRemote) {
                        TextComponentTranslation translation = new TextComponentTranslation("divineadditions.message.current_knowledge_level_changed", level);
                        translation.getStyle().setColor(TextFormatting.GRAY);
                        playerIn.sendMessage(translation);
                    }

                    return ActionResult.newResult(EnumActionResult.SUCCESS, heldItem);
                }
            }
        }

        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        TextComponentTranslation translation = new TextComponentTranslation("divineadditions.tooltip.knowledge_level", level);
        translation.getStyle().setColor(TextFormatting.GRAY);
        tooltip.add(translation.getFormattedText());

        NBTTagCompound compound = stack.getTagCompound();
        if (compound != null) {
            String name = compound.getString(entityName);
            if (!name.isEmpty()) {
                tooltip.add(TextFormatting.GRAY + I18n.translateToLocalFormatted("book.byAuthor", name));
            }
        }
    }
}
