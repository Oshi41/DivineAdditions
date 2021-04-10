package divineadditions.recipe;

import divineadditions.recipe.ingredient.RemainingIngredient;
import net.minecraft.item.crafting.IRecipe;

import java.util.List;

public interface ISpecialRecipe extends IRecipe {

    /**
     * Reutns list of remaining ingredients
     *
     * @return
     */
    List<RemainingIngredient> getRemaining();
}
