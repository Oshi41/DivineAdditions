package divineadditions.recipe;

import com.google.gson.JsonObject;
import divineadditions.capability.knowledge.IKnowledgeInfo;
import divineadditions.recipe.ingredient.RemainingIngredient;
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

import java.util.List;
import java.util.stream.Collectors;

public class SpecialShapeless extends ShapelessRecipes implements ISpecialRecipe {
    private final int level;
    private final List<RemainingIngredient> remaining;

    public SpecialShapeless(String group, ItemStack output, NonNullList<Ingredient> ingredients, int level) {
        super(group, output, ingredients);
        this.level = level;

        remaining = getIngredients().stream().filter(x -> x instanceof RemainingIngredient)
                .map(x -> ((RemainingIngredient) x))
                .collect(Collectors.toList());
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
        if (capability == null || capability.getLevel() < level)
            return false;

        return super.matches(inv, worldIn);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        NonNullList<ItemStack> stacks = super.getRemainingItems(inv);

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack itemStack = inv.getStackInSlot(i).copy();

            if (getRemaining().stream().anyMatch(x -> x.apply(itemStack))) {
                stacks.set(i, itemStack);
            }
        }

        return stacks;
    }

    @Override
    public List<RemainingIngredient> getRemaining() {
        return remaining;
    }
}
