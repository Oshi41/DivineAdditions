package divineadditions.jei.recipe_wrapper.rifle;

import divineadditions.config.DivineAdditionsConfig;
import divineadditions.holders.Items;
import divineadditions.jei.category.RifleCoreCategory;
import divineadditions.jei.recipe_wrapper.ForgeRecipeWrapper;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.Collections;
import java.util.List;

public class RifleMobRecipeWrapper extends RifleRecipeWrapper {
    private final List<ItemStack> cageMobStacks;
    private final IDrawableStatic arrowWithSlot;

    public RifleMobRecipeWrapper(IGuiHelper helper) {
        super(helper, Items.rifle_mob_core, DivineAdditionsConfig.rifleConfig.modCoreConfig);
        cageMobStacks = ForgeRecipeWrapper.createCageMobStacks();
        arrowWithSlot = helper.createDrawable(new ResourceLocation("jei", "textures/gui/gui_vanilla.png"), 67, 168, 58, 18);
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        arrowWithSlot.draw(minecraft, 67 + 18, RifleCoreCategory.stacksSpacing);
    }

    @Override
    public void getIngredients(IIngredients iIngredients) {
        super.getIngredients(iIngredients);
        iIngredients.setOutputLists(VanillaTypes.ITEM, Collections.singletonList(cageMobStacks));
    }

    @Override
    public void onTooltip(int slotIndex, boolean input, ItemStack ingredient, List<String> tooltip) {
        super.onTooltip(slotIndex, input, ingredient, tooltip);

        if (!input) {
            tooltip.add("");
            tooltip.add(new TextComponentTranslation("divineadditions.jei.rifle_core.caged_mob").getFormattedText());
        }
    }
}
