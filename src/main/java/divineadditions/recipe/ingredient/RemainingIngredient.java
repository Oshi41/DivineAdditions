package divineadditions.recipe.ingredient;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import javax.annotation.Nullable;

public class RemainingIngredient extends Ingredient {
    public final int damage;

    public RemainingIngredient(ItemStack stack, int damage) {
        super(stack);
        this.damage = damage;
    }

    @Override
    public boolean apply(@Nullable ItemStack stack) {
        if (stack == null)
            return false;

        for (ItemStack itemStack : getMatchingStacks()) {
            if (itemStack.getItem().equals(stack.getItem()))
                return true;
        }

        return false;
    }
}
