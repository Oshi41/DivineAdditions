package divineadditions.jei.category;

import divineadditions.DivineAdditions;
import divineadditions.gui.conainter.ForgeContainer;
import divineadditions.gui.gui_container.ForgeGuiContainer;
import divineadditions.holders.Blocks;
import divineadditions.jei.recipe.IForgeRecipeWrapper;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * See {@link ForgeGuiContainer}
 */
public class ForgeRecipeCategory implements IRecipeCategory {
    public static final ResourceLocation ID = new ResourceLocation(DivineAdditions.MOD_ID, "tile.forge.name");
    public static final ResourceLocation Background = new ResourceLocation(DivineAdditions.MOD_ID, "textures/gui/forge.png");
    private final IDrawableStatic background;
    private final IDrawable drawableIcon;
    private final String title;

    private final IDrawable blankDrawable;
    private final IDrawable slot;

    public ForgeRecipeCategory(IGuiHelper helper) {
        blankDrawable = helper.createBlankDrawable(189, 196 + 24);
        background = helper.createDrawable(Background, 0, 0, 189, 196);
        drawableIcon = helper.createDrawableIngredient(new ItemStack(Blocks.forge));
        slot = helper.getSlotDrawable();
        title = I18n.format(ID.getResourcePath());
    }

    @Override
    public String getUid() {
        return ID.toString();
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getModName() {
        return ID.getResourceDomain();
    }

    @Override
    public IDrawable getBackground() {
        return blankDrawable;
    }

    @Nullable
    @Override
    public IDrawable getIcon() {
        return drawableIcon;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        background.draw(minecraft, 0, 24);
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
        if (!(iRecipeWrapper instanceof IForgeRecipeWrapper)) {
            DivineAdditions.logger.warn("Recipe should implement IForgeRecipeWrapper interface");
            return;
        }

        IGuiItemStackGroup itemStacks = iRecipeLayout.getItemStacks();
        IForgeRecipeWrapper forgeRecipe = (IForgeRecipeWrapper) iRecipeWrapper;
        itemStacks.addTooltipCallback(forgeRecipe);
        int craftGridSize = forgeRecipe.getCraftGridSize();

        int i = 0;

        while (i < craftGridSize * craftGridSize) {
            int x = i % craftGridSize;
            int y = i / craftGridSize;

            itemStacks.init(i++, true, 30 + x * 18, 12 + 24 + y * 18);
        }

        // caged mob
        itemStacks.init(i++, true, 5, 84 + 24);

        // output
        itemStacks.init(i++, false, 156, 52 + 24);

        int start = craftGridSize * craftGridSize + 1;
        int end = iIngredients.getInputs(VanillaTypes.ITEM).size();

        for (int j = start; j < end; j++) {
            itemStacks.init(i++, true, (j - start) * 18, 0);
            itemStacks.setBackground(i - 1, slot);
        }

        itemStacks.set(iIngredients);

        Map<Integer, ? extends IGuiIngredient<ItemStack>> guiIngredients = itemStacks.getGuiIngredients();

        // clear items NOT from crafting grid
        for (int j = craftGridSize * craftGridSize; j < guiIngredients.size(); j++) {
            guiIngredients.get(j).getAllIngredients().clear();
        }
    }
}
