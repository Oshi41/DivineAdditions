package divineadditions.jei;

import divineadditions.recipe.ForgeRecipes;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.List;
import java.util.stream.Collectors;

@JEIPlugin
public class JeiModule implements IModPlugin {

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        registry.addRecipeCategories(new ForgeRecipeCategory(guiHelper));
    }

    @Override
    public void register(IModRegistry registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        List<ForgeRecipes> recipesList = ForgeRegistries.RECIPES.getValuesCollection().stream().filter(x -> x instanceof ForgeRecipes).map(x -> ((ForgeRecipes) x)).collect(Collectors.toList());
        registry.addRecipes(recipesList.stream().map(x -> new ForgeRecipeWrapper(x, guiHelper)).collect(Collectors.toList()), ForgeRecipeCategory.ID.toString());
    }
}
