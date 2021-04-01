package divineadditions.jei;

import com.sun.javafx.geom.Rectangle;
import divineadditions.api.IEntityCage;
import divineadditions.holders.Items;
import divineadditions.recipe.ForgeRecipes;
import divineadditions.recipe.ingredient.NbtIngredient;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import scala.actors.threadpool.Arrays;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ForgeRecipeWrapper implements IRecipeWrapper {
    private static final Rectangle dnaFillingRectOut = new Rectangle(190, 16, 12, 53);
    private static final Rectangle dnaFillingRect = new Rectangle(7, 16, 12, 53);

    private final ItemStack output;
    private final NonNullList<Ingredient> ingredients;
    private final NonNullList<NbtIngredient> catalystIngredients;
    private final int dna;
    private final int experience;
    private final int knowledgeLevel;

    private final IDrawableAnimated dnaAnimable;

    public ForgeRecipeWrapper(ForgeRecipes recipes, IGuiHelper helper) {
        output = recipes.getRecipeOutput();
        ingredients = recipes.getIngredients();
        dna = recipes.getDna();
        experience = recipes.getExperience();
        knowledgeLevel = recipes.getKnowledgeLevel();
        catalystIngredients = recipes.getCatalystIngredients();

        dnaAnimable = helper.drawableBuilder(ForgeRecipeCategory.Background, dnaFillingRectOut.x, dnaFillingRectOut.y, dnaFillingRectOut.width, dnaFillingRectOut.height)
                .buildAnimated(100, IDrawableAnimated.StartDirection.TOP, false);
    }

    @Override
    public void getIngredients(IIngredients iIngredients) {
        iIngredients.setOutput(VanillaTypes.ITEM, output);

        List<List<ItemStack>> lists = new ArrayList<>();

        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            lists.add(i, Arrays.asList(ingredient.getMatchingStacks()));
        }

        // caged mod
        {
            ArrayList<ItemStack> stacks = new ArrayList<>();
            List<String> allEntityNames = ForgeRegistries
                    .ENTITIES
                    .getValuesCollection()
                    .stream()
                    .filter(x -> EntityMob.class.isAssignableFrom(x.getEntityClass()))
                    .limit(20)
                    .map(x -> x.getRegistryName().toString())
                    .collect(Collectors.toList());

            for (String entityName : allEntityNames) {
                ItemStack stack = new ItemStack(Items.caged_mob);
                stack.getOrCreateSubCompound(IEntityCage.cagedTagName).setString("id", entityName);
                stacks.add(stack);
            }

            lists.add(stacks);
        }

        for (int i = 0; i < catalystIngredients.size(); i++) {
            NbtIngredient ingredient = catalystIngredients.get(i);
            lists.add(ingredients.size() + i + 1, Collections.singletonList(ingredient.getStack()));
        }

        iIngredients.setInputLists(VanillaTypes.ITEM, lists);
    }

    /**
     * See {@link divineadditions.gui.gui_container.ForgeGuiContainer}
     *
     * @param minecraft
     * @param recipeWidth
     * @param recipeHeight
     * @param mouseX
     * @param mouseY
     */
    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        dnaAnimable.draw(minecraft, dnaFillingRect.x, dnaFillingRect.y);
        ITextComponent text = null;

        if (experience > 0) {
            text = new TextComponentTranslation("divineadditions.gui.xp_level_needed").appendText(" " + experience);
            minecraft.fontRenderer.drawSplitString(text.getFormattedText(), 129, 80, 53, Color.GREEN.getRGB());
        }

        text = new TextComponentTranslation("divineadditions.gui.knowlegde_level").appendText(" ").appendSibling(new TextComponentTranslation("enchantment.level." + knowledgeLevel));
        minecraft.fontRenderer.drawSplitString(text.getFormattedText(), 129, 10, 53, 10526880);
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        ArrayList<String> list = new ArrayList<>();

        if (dnaFillingRect.contains(mouseX, mouseY)) {
            list.add("DNA amount: " + dna);
        }

        return list;
    }
}
