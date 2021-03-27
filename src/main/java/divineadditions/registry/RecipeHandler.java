package divineadditions.registry;

import com.google.gson.JsonObject;
import divineadditions.DivineAdditions;
import divineadditions.recipe.InfusingRecipe;
import divineadditions.recipe.SpecialShaped;
import divineadditions.recipe.ingredient.NbtIngredient;
import divineadditions.recipe.ingredient.RemainingIngredient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Mod.EventBusSubscriber(modid = DivineAdditions.MOD_ID)
public class RecipeHandler {
    private static final Map<ResourceLocation, IRecipeFactory> recipeMap = new HashMap<ResourceLocation, IRecipeFactory>() {{
        put(new ResourceLocation(DivineAdditions.MOD_ID, "smelting"), (context, json) -> {

            JsonObject ingredientJson = JsonUtils.getJsonObject(json, "ingredient");
            JsonObject resultJson = JsonUtils.getJsonObject(json, "result");
            int experience = JsonUtils.getInt(json, "experience");

            ItemStack ingredientStack = CraftingHelper.getItemStack(ingredientJson, context);
            ItemStack resultStack = CraftingHelper.getItemStack(resultJson, context);

            FurnaceRecipes furnaceRecipes = FurnaceRecipes.instance();
            furnaceRecipes.addSmeltingRecipe(ingredientStack, resultStack, experience);

            return null;
        });

        put(new ResourceLocation(DivineAdditions.MOD_ID, "infusing"), (context, json) -> {
            String group = JsonUtils.getString(json, "group", "");
            Ingredient catalyst = CraftingHelper.getIngredient(JsonUtils.getJsonObject(json, "catalyst"), context);
            ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);

            Ingredient[] ingredients = StreamSupport.stream(JsonUtils.getJsonArray(json, "ingredients").spliterator(), false)
                    .map(x -> CraftingHelper.getIngredient(x, context))
                    .toArray(Ingredient[]::new);

            String type = JsonUtils.getString(json, "infusingType", "lightning");
            return new InfusingRecipe(group, result, NonNullList.from(Ingredient.EMPTY, ingredients), catalyst, type);
        });

        put(new ResourceLocation(DivineAdditions.MOD_ID, "shaped"), SpecialShaped::deserialize);
    }};

    private static final Map<ResourceLocation, IIngredientFactory> ingredientMap = new HashMap<ResourceLocation, IIngredientFactory>() {{
        put(new ResourceLocation(DivineAdditions.MOD_ID, "remaining_item"), (context, json) -> {
            ItemStack stack = CraftingHelper.getItemStack(json, context);
            int damage = JsonUtils.getInt(json, "damage", 0);
            return new RemainingIngredient(stack, damage);
        });

        put(new ResourceLocation(DivineAdditions.MOD_ID, "item_nbt"), (context, json) -> {
            ItemStack itemStack = CraftingHelper.getItemStack(json, context);
            int withCount = JsonUtils.getInt(json, "withCount", -1);
            if (withCount > 0) {
                itemStack.setCount(withCount);
            }
            return new NbtIngredient(itemStack, withCount > 0);
        });
    }};

    @SubscribeEvent()
    public static void registerRecipe(final RegistryEvent.Register<IRecipe> event) {
        File recipesFile = new File(Loader.instance().getConfigDir(), DivineAdditions.MOD_ID + "/recipes");
        if (recipesFile.exists() && recipesFile.listFiles() != null) {
            List<File> recipeFiles = Arrays.stream(recipesFile.listFiles()).filter(x -> x.getName().endsWith(".json")).collect(Collectors.toList());
            if (!recipeFiles.isEmpty()) {
                ingredientMap.forEach(CraftingHelper::register);
                recipeMap.forEach(CraftingHelper::register);

                IForgeRegistry<IRecipe> registry = event.getRegistry();

                JsonContext ctx = new JsonContext(DivineAdditions.MOD_ID);

                for (File file : recipeFiles) {
                    try {
                        JsonObject json = CraftingHelper.GSON.fromJson(FileUtils.readFileToString(file, StandardCharsets.UTF_8), JsonObject.class);
                        if (!CraftingHelper.processConditions(json, "conditions", ctx))
                            continue;

                        if (json.has("type") && recipeMap.containsKey(new ResourceLocation(JsonUtils.getString(json, "type")))) {
                            IRecipe recipe = CraftingHelper.getRecipe(json, ctx);
                            if (recipe != null) {
                                registry.register(recipe.setRegistryName(new ResourceLocation(DivineAdditions.MOD_ID, file.getName())));
                            }
                        }
                    } catch (Exception e) {
                        DivineAdditions.logger.warn(e);
                    }
                }
            }
        }
    }
}
