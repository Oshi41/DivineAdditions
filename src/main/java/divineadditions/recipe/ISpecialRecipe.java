package divineadditions.recipe;

import divineadditions.recipe.ingredient.NbtIngredient;
import divineadditions.recipe.ingredient.RemainingIngredient;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ForgeHooks;

import java.util.List;
import java.util.stream.Collectors;

public interface ISpecialRecipe extends IRecipe {

    /**
     * Reutns list of remaining ingredients
     *
     * @return
     */
    default List<RemainingIngredient> getRemaining() {
        return getIngredients().stream().filter(x -> x instanceof RemainingIngredient).map(x -> ((RemainingIngredient) x)).collect(Collectors.toList());
    }

    /**
     * Returns list of NBT ingredients
     *
     * @return
     */
    default List<NbtIngredient> getNbtIngredient() {
        return getIngredients().stream().filter(x -> x instanceof NbtIngredient).map(x -> ((NbtIngredient) x)).collect(Collectors.toList());
    }

    default NonNullList<ItemStack> remainingItems(IInventory inventory) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(getIngredients().size(), ItemStack.EMPTY);

        List<RemainingIngredient> remaining = getRemaining();
        List<NbtIngredient> nbtIngredient = getNbtIngredient();

        for (int i = 0; i < nonnulllist.size(); ++i) {
            final ItemStack itemstack = inventory.getStackInSlot(i);
            final ItemStack containerItem = ForgeHooks.getContainerItem(itemstack);

            if (remaining.stream().anyMatch(x -> x.apply(itemstack))) {
                // ignored
            } else {
                NbtIngredient ingredient = nbtIngredient.stream().filter(x -> x.apply(itemstack)).findFirst().orElse(null);
                if (ingredient != null) {
                    itemstack.shrink(ingredient.getStack().getCount());
                } else {
                    itemstack.shrink(1);
                }
            }

            ItemStack result = itemstack;

            if (result.isEmpty()) {
                result = containerItem;
            }

            nonnulllist.set(i, result);
        }

        return nonnulllist;
    }
}
