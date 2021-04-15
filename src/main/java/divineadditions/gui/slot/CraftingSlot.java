package divineadditions.gui.slot;

import com.google.common.collect.Lists;
import divineadditions.DivineAdditions;
import divineadditions.api.IContainerSync;
import divineadditions.api.IForgeInventory;
import divineadditions.msg.ChangeRecipeMsg;
import divineadditions.recipe.ForgeRecipes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

public class CraftingSlot extends Slot implements IContainerSync {

    private final EntityPlayer player;
    private final IForgeInventory handler;
    /**
     * The number of items that have been crafted so far. Gets passed to ItemStack.onCrafting before being reset.
     */
    private int amountCrafted;
    private ForgeRecipes currentRecipe;

    public CraftingSlot(EntityPlayer player, InventoryCraftResult inventoryIn, IForgeInventory handler, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
        this.player = player;
        this.handler = handler;
    }

    @Override
    public ItemStack getStack() {
        return super.getStack();
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return false;
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
     * stack.
     */
    @Override
    public ItemStack decrStackSize(int amount) {
        if (this.getHasStack()) {
            this.amountCrafted += Math.min(amount, this.getStack().getCount());
        }

        return super.decrStackSize(amount);
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood. Typically increases an
     * internal count then calls onCrafting(item).
     */
    @Override
    protected void onCrafting(ItemStack stack, int amount) {
        this.amountCrafted += amount;
        this.onCrafting(stack);
    }

    @Override
    protected void onSwapCraft(int count) {
        amountCrafted += count;
    }

    @Override
    protected void onCrafting(ItemStack stack) {
        if (this.amountCrafted > 0) {
            stack.onCrafting(this.player.world, this.player, this.amountCrafted);
            net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerCraftingEvent(this.player, stack, inventory);
        }

        this.amountCrafted = 0;
        InventoryCraftResult inventorycraftresult = (InventoryCraftResult) this.inventory;
        IRecipe irecipe = inventorycraftresult.getRecipeUsed();

        if (irecipe != null && !irecipe.isDynamic()) {
            this.player.unlockRecipes(Lists.newArrayList(irecipe));
            inventorycraftresult.setRecipeUsed(null);
        }
    }

    public void setCurrentRecipe(ForgeRecipes recipe) {
        if (!Objects.equals(recipe, this.currentRecipe)) {
            this.currentRecipe = recipe;


            if (player instanceof EntityPlayerMP) {
                DivineAdditions.networkWrapper.sendTo(createMessage(), ((EntityPlayerMP) player));
            }
        }
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        if (currentRecipe == null)
            return false;

        boolean canTake = currentRecipe.matchesWithoutGrid(handler, playerIn.getEntityWorld());
        return canTake;
    }

    @Override
    public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
        IRecipe recipe = ((InventoryCraftResult) this.inventory).getRecipeUsed();
        this.onCrafting(stack);

        if (recipe instanceof ForgeRecipes) {
            return handleRecipe(((ForgeRecipes) recipe), thePlayer, stack, handler);
        }

        return stack;
    }

    protected ItemStack handleRecipe(ForgeRecipes recipes, EntityPlayer thePlayer, ItemStack result, IForgeInventory inventory) {
        ForgeHooks.setCraftingPlayer(thePlayer);

        NonNullList<ItemStack> grid = recipes.getRemainingItemsFromCraftingGrid(inventory);
        Map<TileEntity, NonNullList<ItemStack>> catalysts = recipes.getRemainingItemsForCatalysts(inventory);

        ForgeHooks.setCraftingPlayer(null);

        replace(((IItemHandlerModifiable) inventory.getCurrentHandler()), grid);

        for (Map.Entry<TileEntity, NonNullList<ItemStack>> entry : catalysts.entrySet()) {
            IItemHandlerModifiable capability = (IItemHandlerModifiable) entry.getKey().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            replace(capability, entry.getValue());
        }

        if (recipes.getExperience() > 0) {
            player.addExperienceLevel(-recipes.getExperience());
        }

        if (recipes.getDna() > 0) {
            inventory.getCurrentDna().drain(recipes.getDna(), true);
        }

        // recalculate craft result
        player.openContainer.onCraftMatrixChanged(this.inventory);

        return result;
    }

    private void replace(IItemHandlerModifiable handler, NonNullList<ItemStack> remaining) {
        for (int i = 0; i < remaining.size(); ++i) {
            handler.setStackInSlot(i, remaining.get(i));
        }
    }

    @Nullable
    @Override
    public IMessage createMessage() {
        return new ChangeRecipeMsg(currentRecipe);
    }
}
