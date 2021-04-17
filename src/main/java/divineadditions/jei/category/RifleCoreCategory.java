package divineadditions.jei.category;

import divineadditions.DivineAdditions;
import divineadditions.holders.Items;
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
import java.util.List;

public class RifleCoreCategory implements IRecipeCategory {
    public static final int recipeWidth = 160;
    public static final int recipeHeight = 125;
    public static final int stacksSpacing = 24;
    public static String ID = DivineAdditions.MOD_ID + ".rifle";
    private final IDrawable icon;
    private final IDrawableStatic background;
    private final String title;
    private final IDrawableStatic slotBackground;
    private final IDrawableStatic backgroundSlots;

    public RifleCoreCategory(IGuiHelper helper) {
        icon = helper.createDrawableIngredient(new ItemStack(Items.rifle));
        background = helper.createBlankDrawable(recipeWidth, recipeHeight);
        backgroundSlots = helper.createDrawable(new ResourceLocation("jei", "textures/gui/gui_vanilla.png"), 0, 168, 67, 18);
        title = I18n.format("item.rifle.name");

        slotBackground = helper.getSlotDrawable();
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
        backgroundSlots.draw(minecraft, 18, stacksSpacing);
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, IRecipeWrapper iRecipeWrapper, IIngredients iIngredients) {

        IGuiItemStackGroup itemStacks = iRecipeLayout.getItemStacks();

        int xPos = 18 + 49;
        itemStacks.init(0, true, xPos, 0);
        itemStacks.setBackground(0, slotBackground);

        if (iRecipeWrapper instanceof ITooltipCallback) {
            itemStacks.addTooltipCallback(((ITooltipCallback) iRecipeWrapper));
        }

        List<List<ItemStack>> ingredientsInputs = iIngredients.getInputs(VanillaTypes.ITEM);

        for (int i = 1; i < ingredientsInputs.size(); i++) {
            itemStacks.init(i, true, 18 + (49 * (i - 1)), stacksSpacing);
            //itemStacks.setBackground(i, slotBackground);
        }

        List<List<ItemStack>> outputs = iIngredients.getOutputs(VanillaTypes.ITEM);

        for (int i = 0; i < outputs.size(); i++) {
            int index = i + ingredientsInputs.size();
            itemStacks.init(index, false, 107 + 18 + (i * 18), stacksSpacing);
            //itemStacks.setBackground(index, slotBackground);
        }

        itemStacks.set(iIngredients);
    }
}
