package divineadditions.api;

import divineadditions.DivineAdditions;
import divineadditions.utils.NbtUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.IItemHandler;

import java.util.Map;

public interface IForgeInventory {
    String ForgeLevelName = "ForgeLevel";

    /**
     * Get crafting height
     *
     * @return
     */
    int getHeight();

    /**
     * Get crafting width
     *
     * @return
     */
    int getWidth();

    /**
     * Current DNA for usage
     *
     * @return
     */
    IFluidTank getCurrentDna();

    /**
     * Returns current stacks holder
     *
     * @return
     */
    IItemHandler getCurrentHandler();

    /**
     * Current crafting player
     *
     * @return
     */
    EntityPlayer getCraftingPlayer();

    /**
     * List of catalyst stands attached to forge
     *
     * @return
     */
    Map<TileEntity, IItemHandler> findCatalystStands();

    /**
     * Current level of crafting
     *
     * @return
     */
    default int getCurrentLevel() {
        EntityPlayer player = getCraftingPlayer();

        if (player != null) {
            NBTTagCompound tag = NbtUtils.getOrCreateModPlayerPersistTag(player, DivineAdditions.MOD_ID);
            if (tag.hasKey(ForgeLevelName)) {
                return tag.getInteger(ForgeLevelName);
            }
        }

        return -1;
    }

    /**
     * Crafting slots are always first
     *
     * @param row
     * @param column
     * @return
     */
    default ItemStack getStackInRowAndColumn(int row, int column) {
        return row >= 0 && row < getWidth() && column >= 0 && column <= getHeight()
                ? getCurrentHandler().getStackInSlot(row + column * getWidth())
                : ItemStack.EMPTY;
    }

    void openInventory(EntityPlayer player);

    void closeInventory(EntityPlayer player);

    boolean canInteractWith(EntityPlayer playerIn);
}
