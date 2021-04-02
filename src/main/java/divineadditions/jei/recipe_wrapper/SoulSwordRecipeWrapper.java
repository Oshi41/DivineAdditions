package divineadditions.jei.recipe_wrapper;

import divineadditions.holders.Items;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SoulSwordRecipeWrapper implements IRecipeWrapper {
    @Override
    public void getIngredients(IIngredients iIngredients) {
        List<ItemStack> swords = new ArrayList<>();

        for (int i = 0; i <= 3; i++) {
            ItemStack stack = new ItemStack(Items.soul_sword);

            if (i > 0) {
                Map<Enchantment, Integer> enchMap = new HashMap<>();
                enchMap.put(Enchantments.LOOTING, i);
                EnchantmentHelper.setEnchantments(enchMap, stack);
            }

            swords.add(stack);
        }

        ArrayList<List<ItemStack>> result = new ArrayList<>();
        result.add(swords);
        iIngredients.setInputLists(VanillaTypes.ITEM, result);

        result = new ArrayList<>();
        result.add(ForgeRecipeWrapper.createCageMobStacks());

        iIngredients.setOutputLists(VanillaTypes.ITEM, result);
    }
}
