package divineadditions.jei;

import divineadditions.holders.Blocks;
import divineadditions.holders.Items;
import divineadditions.jei.category.ForgeRecipeCategory;
import divineadditions.jei.category.RifleCoreCategory;
import divineadditions.jei.category.SoulSwordCategory;
import divineadditions.jei.recipe_wrapper.ForgeRecipeWrapper;
import divineadditions.jei.recipe_wrapper.SoulSwordRecipeWrapper;
import divineadditions.jei.recipe_wrapper.rifle.RifleMobRecipeWrapper;
import divineadditions.recipe.ForgeRecipes;
import mezz.jei.api.*;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@JEIPlugin
public class JeiModule implements IModPlugin {

    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
        subtypeRegistry.useNbtForSubtypes(Items.caged_mob, Items.soul_sword, Items.rifle_mob_core);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        registry.addRecipeCategories(new ForgeRecipeCategory(guiHelper), new SoulSwordCategory(guiHelper), new RifleCoreCategory(guiHelper));
    }

    @Override
    public void register(IModRegistry registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();

        List<ForgeRecipes> recipesList = ForgeRegistries
                .RECIPES
                .getValuesCollection()
                .stream()
                .filter(x -> x instanceof ForgeRecipes)
                .map(x -> ((ForgeRecipes) x))
                .collect(Collectors.toList());


        registry.addRecipes(recipesList.stream().map(x -> new ForgeRecipeWrapper(x, guiHelper))
                .collect(Collectors.toList()), ForgeRecipeCategory.ID.toString());

        registry.addRecipes(Arrays.asList(new SoulSwordRecipeWrapper()), SoulSwordCategory.ID);

        registry.addRecipes(Arrays.asList(new RifleMobRecipeWrapper(guiHelper)), RifleCoreCategory.ID);

        registry.addIngredientInfo(new ItemStack(Blocks.time_beacon), VanillaTypes.ITEM,
                "divineadditions.tooltip.time_beacon");
    }
}
