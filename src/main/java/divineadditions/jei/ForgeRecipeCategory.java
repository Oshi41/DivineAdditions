package divineadditions.jei;

import divineadditions.DivineAdditions;
import divineadditions.gui.conainter.ForgeContainer;
import divineadditions.gui.gui_container.ForgeGuiContainer;
import divineadditions.holders.Blocks;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;

/**
 * See {@link ForgeGuiContainer}
 */
public class ForgeRecipeCategory implements IRecipeCategory {
    public static final ResourceLocation ID = new ResourceLocation(DivineAdditions.MOD_ID, "tile.forge.name");
    public static final ResourceLocation Background = new ResourceLocation(DivineAdditions.MOD_ID, "textures/gui/forge.png");
    private final IDrawableStatic drawable;
    private final IDrawable drawableIcon;

    public ForgeRecipeCategory(IGuiHelper helper) {
        drawable = helper.createDrawable(Background, 0, 0, 189, 196);
        drawableIcon = helper.createDrawableIngredient(new ItemStack(Blocks.forge));
    }

    @Override
    public String getUid() {
        return ID.toString();
    }

    @Override
    public String getTitle() {
        return I18n.format(ID.getResourcePath());
    }

    @Override
    public String getModName() {
        return ID.getResourceDomain();
    }

    @Override
    public IDrawable getBackground() {
        return drawable;
    }

    @Nullable
    @Override
    public IDrawable getIcon() {
        return drawableIcon;
    }

    /**
     * See {@link ForgeContainer#drawSlots()}
     *
     * @param iRecipeLayout
     * @param iRecipeWrapper
     * @param iIngredients
     */
    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, IRecipeWrapper iRecipeWrapper, IIngredients iIngredients) {
        IGuiItemStackGroup itemStacks = iRecipeLayout.getItemStacks();
        int i = 0;

        while (i < 25) {
            int x = i % 5;
            int y = i / 5;

            itemStacks.init(i++, true, 30 + x * 18, 12 + y * 18);
        }

        // caged mob
        itemStacks.init(i++, true, 5, 84);

        // output
        itemStacks.init(i++, false, 156, 52);

        List<List<ItemStack>> lists = iIngredients.getInputs(VanillaTypes.ITEM);

        // catalyts
        for (int j = 26; j < lists.size(); j++) {
            itemStacks.init(i++, true, 195, 3 + (j - 26) * 18);
        }

        itemStacks.set(iIngredients);
    }
}
