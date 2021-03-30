package divineadditions.recipe;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import divineadditions.recipe.ingredient.RemainingIngredient;
import divineadditions.utils.NbtUtils;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SpecialShaped extends ShapedRecipes {
    public final List<RemainingIngredient> remaining;

    public SpecialShaped(String group, int width, int height, NonNullList<Ingredient> ingredients, ItemStack result) {
        super(group, width, height, ingredients, result);

        remaining = getIngredients().stream().filter(x -> x instanceof RemainingIngredient)
                .map(x -> ((RemainingIngredient) x))
                .collect(Collectors.toList());
    }

    public static SpecialShaped deserialize(JsonContext context, JsonObject json) {
        String group = JsonUtils.getString(json, "group", "");
        Map<String, Ingredient> map = deserializeKey(context, JsonUtils.getJsonObject(json, "key"));
        String[] patterns = shrink(patternFromJson(JsonUtils.getJsonArray(json, "pattern")));
        int i = patterns[0].length();
        int j = patterns.length;
        NonNullList<Ingredient> ingredients = deserializeIngredients(patterns, map, i, j);
        ItemStack result = NbtUtils.parseStack(JsonUtils.getJsonObject(json, "result"), context);

        return new SpecialShaped(group, i, j, ingredients, result);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        NonNullList<ItemStack> stacks = super.getRemainingItems(inv);

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack itemStack = inv.getStackInSlot(i).copy();

            if (remaining.stream().anyMatch(x -> x.apply(itemStack))) {
                stacks.set(i, itemStack);
            }
        }

        return stacks;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        boolean result = super.matches(inv, worldIn);
        return result;
    }

    // region Legacy

    private static Map<String, Ingredient> deserializeKey(JsonContext context, JsonObject json) {
        Map<String, Ingredient> map = Maps.newHashMap();

        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (entry.getKey().length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only).");
            }

            if (" ".equals(entry.getKey())) {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }

            map.put(entry.getKey(), CraftingHelper.getIngredient(entry.getValue(), context));
        }

        map.put(" ", Ingredient.EMPTY);
        return map;
    }

    static String[] shrink(String... p_194134_0_) {
        int i = Integer.MAX_VALUE;
        int j = 0;
        int k = 0;
        int l = 0;

        for (int i1 = 0; i1 < p_194134_0_.length; ++i1) {
            String s = p_194134_0_[i1];
            i = Math.min(i, firstNonSpace(s));
            int j1 = lastNonSpace(s);
            j = Math.max(j, j1);

            if (j1 < 0) {
                if (k == i1) {
                    ++k;
                }

                ++l;
            } else {
                l = 0;
            }
        }

        if (p_194134_0_.length == l) {
            return new String[0];
        } else {
            String[] astring = new String[p_194134_0_.length - l - k];

            for (int k1 = 0; k1 < astring.length; ++k1) {
                astring[k1] = p_194134_0_[k1 + k].substring(i, j + 1);
            }

            return astring;
        }
    }

    private static int firstNonSpace(String str) {
        int i;

        for (i = 0; i < str.length() && str.charAt(i) == ' '; ++i) {
        }

        return i;
    }

    private static int lastNonSpace(String str) {
        int i;

        for (i = str.length() - 1; i >= 0 && str.charAt(i) == ' '; --i) {
        }

        return i;
    }

    private static String[] patternFromJson(JsonArray array) {
        String[] astring = new String[array.size()];

        if (astring.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        } else {
            for (int i = 0; i < astring.length; ++i) {
                String s = JsonUtils.getString(array.get(i), "pattern[" + i + "]");

                if (i > 0 && astring[0].length() != s.length()) {
                    throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                }

                astring[i] = s;
            }

            return astring;
        }
    }

    private static NonNullList<Ingredient> deserializeIngredients(String[] pattern, Map<String, Ingredient> ingredients, int patternWidth, int patternHeight) {
        NonNullList<Ingredient> nonnulllist = NonNullList.withSize(patternWidth * patternHeight, Ingredient.EMPTY);
        Set<String> set = Sets.newHashSet(ingredients.keySet());
        set.remove(" ");

        for (int i = 0; i < pattern.length; ++i) {
            for (int j = 0; j < pattern[i].length(); ++j) {
                String s = pattern[i].substring(j, j + 1);
                Ingredient ingredient = ingredients.get(s);

                if (ingredient == null) {
                    throw new JsonSyntaxException("Pattern references symbol '" + s + "' but it's not defined in the key");
                }

                set.remove(s);
                nonnulllist.set(j + patternWidth * i, ingredient);
            }
        }

        if (!set.isEmpty()) {
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
        } else {
            return nonnulllist;
        }
    }

    // endregion

}
