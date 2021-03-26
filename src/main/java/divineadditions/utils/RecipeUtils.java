package divineadditions.utils;

import com.google.gson.JsonObject;
import divineadditions.DivineAdditions;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.registries.IForgeRegistry;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;

public class RecipeUtils {

    /**
     * Load custom recipe
     * Doe not support _constants.json
     *
     * @param id       - id of recipe factory
     * @param factory  - recipe factory
     * @param registry - recipe registry
     */
    public static void loadRecipeFactory(ResourceLocation id, IRecipeFactory factory, IForgeRegistry<IRecipe> registry) {
        CraftingHelper.register(id, factory);

        ModContainer modContainer = Loader.instance().activeModContainer();
        JsonContext ctx = new JsonContext(modContainer.getModId());

        CraftingHelper.findFiles(modContainer,
                "assets/" + modContainer.getModId() + "/recipes",
                null,
                (root, file) -> {
                    try (BufferedReader reader = Files.newBufferedReader(file)) {
                        JsonObject json = JsonUtils.fromJson(CraftingHelper.GSON, reader, JsonObject.class);
                        if (!CraftingHelper.processConditions(json, "conditions", ctx))
                            return true;

                        if (json.has("type") && id.toString().equals(JsonUtils.getString(json, "type"))) {
                            IRecipe recipe = CraftingHelper.getRecipe(json, ctx);
                            if (recipe != null) {
                                registry.register(recipe);
                            }
                        }
                    } catch (IOException e) {
                        DivineAdditions.logger.warn(e);
                    }
                    return true;
                },
                true,
                true);
    }
}
