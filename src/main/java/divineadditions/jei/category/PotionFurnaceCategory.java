package divineadditions.jei.category;

import divineadditions.DivineAdditions;
import divineadditions.holders.Blocks;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;

public class PotionFurnaceCategory implements IRecipeCategory {
    public static final String ID = new ResourceLocation(DivineAdditions.MOD_ID, "potion_furnace").toString();
    public static final ResourceLocation Background = new ResourceLocation(DivineAdditions.MOD_ID, "textures/gui/potion_furnace.png");
    private final String title;
    private final IDrawable background;
    private final IDrawable drawableIcon;


    public PotionFurnaceCategory(IGuiHelper helper) {
        title = new TextComponentTranslation("tile.potion_furnace.name").getFormattedText();

        // see PotionFurnaceGuiContainer.ctor
        background = helper.createDrawable(Background, 0, 0, 175, 165);
        drawableIcon = helper.createDrawableIngredient(new ItemStack(Blocks.potion_furnace));
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
        return drawableIcon;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        minecraft.fontRenderer.drawString(getTitle(), 5, 5, 4210752, false);
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, IRecipeWrapper iRecipeWrapper, IIngredients iIngredients) {
        // see PotionFurnaceContainer.ctor

        IGuiItemStackGroup stacks = iRecipeLayout.getItemStacks();

        if (iRecipeWrapper instanceof ITooltipCallback) {
            stacks.addTooltipCallback(((ITooltipCallback) iRecipeWrapper));
        }

        int i = 0;

        for (i = 0; i < 3; i++) {
            stacks.init(i, true, 17 + (44 * i), 47);
        }

        // fuel
        stacks.init(i++, true, 146, 51);

        stacks.init(i++, false, 146, 5);

        stacks.set(iIngredients);

        IGuiIngredient<ItemStack> ingredient = stacks.getGuiIngredients().get(3);
        // remove fuel from recipe
        ingredient.getAllIngredients().clear();
    }
}
