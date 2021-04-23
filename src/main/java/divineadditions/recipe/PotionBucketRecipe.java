package divineadditions.recipe;

import divineadditions.DivineAdditions;
import divineadditions.recipe.ingredient.NbtIngredient;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;

import java.util.ArrayList;
import java.util.List;

public class PotionBucketRecipe extends ShapedRecipes {
    protected PotionBucketRecipe(ItemStack potion) {
        super("", 3, 3, createIngredients(potion), createBucket(potion));

        List<PotionEffect> effects = PotionUtils.getEffectsFromStack(potion);
        String name = "";
        if (!effects.isEmpty()) {
            name = "_" + effects.get(0).getEffectName();
        }

        setRegistryName(DivineAdditions.MOD_ID, "enchant_bucket" + name);
    }

    public static List<IRecipe> createRecipes() {
        ArrayList<IRecipe> result = new ArrayList<>();

        for (PotionType potiontype : PotionType.REGISTRY) {
            if (!potiontype.getEffects().isEmpty()) {
                ItemStack potion = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), potiontype);
                result.add(new PotionBucketRecipe(potion));
            }
        }

        return result;
    }

    private static NonNullList<Ingredient> createIngredients(ItemStack potion) {
        NonNullList<Ingredient> list = NonNullList.create();
        Ingredient ingredient = new NbtIngredient(potion, false);

        list.add(ingredient);
        list.add(Ingredient.fromItem(Items.BLAZE_POWDER));
        list.add(ingredient);

        list.add(ingredient);
        list.add(Ingredient.fromItem(Items.BREWING_STAND));
        list.add(ingredient);

        list.add(ingredient);
        list.add(Ingredient.fromItem(Items.WATER_BUCKET));
        list.add(ingredient);

        return list;
    }

    private static ItemStack createBucket(ItemStack potion) {
        List<PotionEffect> effects = PotionUtils.getEffectsFromStack(potion);
        ItemStack result = new ItemStack(divineadditions.holders.Items.potion_bucket);
        result.setTagCompound(divineadditions.utils.PotionUtils.writeEffects(null, effects));
        return result;
    }
}
