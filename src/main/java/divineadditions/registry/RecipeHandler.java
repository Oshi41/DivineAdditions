package divineadditions.registry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import divineadditions.DivineAdditions;
import divineadditions.recipe.ForgeRecipes;
import divineadditions.recipe.PotionBucketRecipe;
import divineadditions.recipe.SpecialShaped;
import divineadditions.recipe.SpecialShapeless;
import divineadditions.recipe.ingredient.NbtIngredient;
import divineadditions.recipe.ingredient.RemainingIngredient;
import divineadditions.utils.NbtUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JsonUtils;
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
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = DivineAdditions.MOD_ID)
public class RecipeHandler {
    private static final Gson GSON = new GsonBuilder().setLenient().disableHtmlEscaping().create();
    private static final Map<ResourceLocation, IRecipeFactory> recipeMap = new HashMap<ResourceLocation, IRecipeFactory>() {{
        put(new ResourceLocation(DivineAdditions.MOD_ID, "smelting"), (context, json) -> {

            JsonObject ingredientJson = JsonUtils.getJsonObject(json, "ingredient");
            JsonObject resultJson = JsonUtils.getJsonObject(json, "result");
            int experience = JsonUtils.getInt(json, "experience");

            ItemStack ingredientStack = NbtUtils.parseStack(ingredientJson, context);
            ItemStack resultStack = NbtUtils.parseStack(resultJson, context);

            FurnaceRecipes furnaceRecipes = FurnaceRecipes.instance();
            furnaceRecipes.addSmeltingRecipe(ingredientStack, resultStack, experience);

            return null;
        });

        put(new ResourceLocation(DivineAdditions.MOD_ID, "shaped"), SpecialShaped::deserialize);
        put(new ResourceLocation(DivineAdditions.MOD_ID, "shapeless"), SpecialShapeless::deserialize);
        put(new ResourceLocation(DivineAdditions.MOD_ID, "forge"), ForgeRecipes::deserialize);
    }};

    private static final Map<ResourceLocation, IIngredientFactory> ingredientMap = new HashMap<ResourceLocation, IIngredientFactory>() {{
        put(new ResourceLocation(DivineAdditions.MOD_ID, "remaining_item"), (context, json) -> {
            ItemStack stack = NbtUtils.parseStack(json, context);
            int damage = JsonUtils.getInt(json, "damage", 0);
            return new RemainingIngredient(stack, damage);
        });

        put(new ResourceLocation(DivineAdditions.MOD_ID, "item_nbt"), (context, json) -> {
            ItemStack itemStack = NbtUtils.parseStack(json, context);
            int withCount = JsonUtils.getInt(json, "withCount", -1);
            if (withCount > 0) {
                itemStack.setCount(withCount);
            }
            return new NbtIngredient(itemStack, withCount > 0);
        });
    }};

    @SubscribeEvent()
    public static void registerRecipe(final RegistryEvent.Register<IRecipe> event) {

        ingredientMap.forEach(CraftingHelper::register);
        recipeMap.forEach(CraftingHelper::register);
        final JsonContext ctx = new JsonContext(DivineAdditions.MOD_ID);
        final IForgeRegistry<IRecipe> registry = event.getRegistry();

        CraftingHelper.findFiles(Loader.instance().activeModContainer(),
                "assets/" + DivineAdditions.MOD_ID + "/custom_recipes",
                null,
                (root, file) -> {
                    try {
                        registerRecipe(file, ctx, registry);
                        return true;
                    } catch (Exception e) {
                        DivineAdditions.logger.warn(e);
                        return false;
                    }
                }, true, true);

        PotionBucketRecipe.createRecipes().forEach(registry::register);
    }

    private static void registerRecipe(Path file, JsonContext ctx, IForgeRegistry<IRecipe> registry) throws IOException {
        JsonObject json = GSON.fromJson(IOUtils.toString(Files.newBufferedReader(file)), JsonObject.class);
        if (!CraftingHelper.processConditions(json, "conditions", ctx))
            return;

        if (json.has("type") && recipeMap.containsKey(new ResourceLocation(JsonUtils.getString(json, "type")))) {
            IRecipe recipe = CraftingHelper.getRecipe(json, ctx);
            if (recipe != null) {
                ResourceLocation id = new ResourceLocation(DivineAdditions.MOD_ID, file.getFileName().toString());
                if (!registry.containsKey(id)) {
                    registry.register(recipe.setRegistryName(id));
                }
            }
        }
    }
}
