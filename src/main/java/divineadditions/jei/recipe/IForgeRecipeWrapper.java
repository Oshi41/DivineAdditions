package divineadditions.jei.recipe;

import mezz.jei.api.gui.ITooltipCallback;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

public interface IForgeRecipeWrapper extends IRecipeWrapper, ITooltipCallback<ItemStack> {
    /**
     * Returns current craft grid size
     *
     * @return
     */
    int getCraftGridSize();
}
