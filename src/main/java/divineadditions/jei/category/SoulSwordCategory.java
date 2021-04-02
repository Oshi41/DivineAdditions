package divineadditions.jei.category;

import divineadditions.DivineAdditions;
import divineadditions.holders.Items;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class SoulSwordCategory implements IRecipeCategory {
    public static final int recipeWidth = 160;
    public static final int recipeHeight = 125;
    public static final String ID = DivineAdditions.MOD_ID + ".info";
    private final String title;
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableStatic backgroundSlots;

    public SoulSwordCategory(IGuiHelper guiHelper) {
        title = I18n.format("item.soul_sword.name");
        backgroundSlots = guiHelper.createDrawable(new ResourceLocation("jei", "textures/gui/gui_vanilla.png"), 49, 168, 76, 18);
        background = guiHelper.createBlankDrawable(recipeWidth, recipeHeight);
        icon = guiHelper.createDrawableIngredient(new ItemStack(Items.soul_sword));
    }

    @Override
    public String getUid() {
        return ID;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getModName() {
        return DivineAdditions.MOD_ID;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Nullable
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        backgroundSlots.draw(minecraft, 18, 18);
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, IRecipeWrapper iRecipeWrapper, IIngredients iIngredients) {
        IGuiItemStackGroup itemStacks = iRecipeLayout.getItemStacks();

        itemStacks.init(0, true, 18, 18);
        itemStacks.init(1, false, 18 + 58, 18);

        itemStacks.set(iIngredients);
    }
}
