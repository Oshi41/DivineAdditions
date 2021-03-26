package divineadditions.registry;

import com.google.gson.JsonObject;
import divineadditions.DivineAdditions;
import divineadditions.utils.RecipeUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = DivineAdditions.MOD_ID)
public class RecipeHandler {
    private static final Map<ResourceLocation, IRecipeFactory> map = new HashMap<ResourceLocation, IRecipeFactory>() {{
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
    }};

    @SubscribeEvent()
    public static void registerRecipe(final RegistryEvent.Register<IRecipe> event) {
        IForgeRegistry<IRecipe> registry = event.getRegistry();
        map.forEach((location, factory) -> RecipeUtils.loadRecipeFactory(location, factory, registry));
    }
}
