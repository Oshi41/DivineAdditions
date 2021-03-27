package divineadditions.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class InfusingRecipe extends ShapelessRecipes {
    public final String type;
    private final Ingredient catalyst;

    public InfusingRecipe(String group, ItemStack output, NonNullList<Ingredient> ingredients, Ingredient catalyst, String type) {
        super(group, output, ingredients);
        this.catalyst = catalyst;
        this.type = type;
    }

    public Ingredient getCatalyst() {
        return catalyst;
    }

    public boolean match(IItemHandler catalyst, Collection<IItemHandler> ingredients) {
        if (catalyst == null || ingredients == null) {
            return false;
        }

        if (!getCatalyst().apply(catalyst.getStackInSlot(0))) {
            return false;
        }

        List<Ingredient> copy = new ArrayList<>(getIngredients());

        if (copy.size() > ingredients.size())
            return false;

        for (IItemHandler handler : ingredients) {
            ItemStack slot = handler.getStackInSlot(0);

            Optional<Ingredient> ingredient = copy.stream().filter(x -> x.apply(slot)).findFirst();
            if (!ingredient.isPresent()) {
                return false;
            } else {
                copy.remove(ingredient.get());
            }
        }


        return copy.size() == 0;
    }
}
