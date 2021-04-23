package divineadditions.recipe;

import com.google.gson.JsonObject;
import divineadditions.capability.knowledge.IKnowledgeInfo;
import divineadditions.utils.InventoryHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.JsonContext;

public class SpecialShapeless extends ShapelessRecipes implements ISpecialRecipe {
    private final int level;

    public SpecialShapeless(String group, ItemStack output, NonNullList<Ingredient> ingredients, int level) {
        super(group, output, ingredients);
        this.level = level;
    }

    public static IRecipe deserialize(JsonContext jsonContext, JsonObject object) {
        ShapelessRecipes recipes = ShapelessRecipes.deserialize(object);
        int level = JsonUtils.getInt(object, "level", 0);
        return new SpecialShapeless(recipes.getGroup(), recipes.getRecipeOutput(), recipes.getIngredients(), level);
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        EntityPlayer player = InventoryHelper.getFrom(inv);
        if (player == null)
            return false;

        IKnowledgeInfo capability = player.getCapability(IKnowledgeInfo.KnowledgeCapability, null);
        if (capability == null || capability.level().get() < level)
            return false;

        return super.matches(inv, worldIn);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        return remainingItems(inv);
    }
}
