package divineadditions.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import divineadditions.DivineAdditions;
import divineadditions.api.IForgeInventory;
import divineadditions.recipe.ingredient.NbtIngredient;
import divineadditions.utils.InventoryHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ForgeRecipes extends SpecialShaped {
    private final int level;
    private final int experience;
    private final int dna;
    private final NonNullList<NbtIngredient> ingredients;

    public ForgeRecipes(SpecialShaped source, int level, int experience, int dna, NonNullList<NbtIngredient> ingredients) {
        super(source.getGroup(), source.getWidth(), source.getHeight(), source.getIngredients(), source.getRecipeOutput(), level);
        this.level = level;
        this.experience = experience;
        this.dna = dna;
        this.ingredients = ingredients;
    }

    /**
     * Deserialize recipe
     *
     * @param context
     * @param json
     * @return
     */
    public static ForgeRecipes deserialize(JsonContext context, JsonObject json) {
        SpecialShaped source = SpecialShaped.deserialize(context, json);
        int level = JsonUtils.getInt(json, "level", 1);
        int experience = JsonUtils.getInt(json, "experience", 0);
        int dna = JsonUtils.getInt(json, "dna", 100);

        JsonArray catalysts = JsonUtils.getJsonArray(json, "catalysts", new JsonArray());

        List<Ingredient> catalystIngredients = StreamSupport.stream(catalysts.spliterator(), false)
                .map(x -> CraftingHelper.getIngredient(x, context))
                .collect(Collectors.toList());

        if (catalystIngredients.stream().anyMatch(x -> !(x instanceof NbtIngredient))) {
            throw new RuntimeException("Ingredient type should be inherited from " + DivineAdditions.MOD_ID + ":nbt_item");
        }

        NonNullList<NbtIngredient> ingredients = NonNullList.withSize(catalystIngredients.size(), new NbtIngredient(ItemStack.EMPTY, false));

        for (int i = 0; i < catalystIngredients.size(); i++) {
            ingredients.set(i, ((NbtIngredient) catalystIngredients.get(i)));
        }

        return new ForgeRecipes(source, level, experience, dna, ingredients);
    }

    /**
     * OVerride to false to prevent it from usual crafting table
     *
     * @param inv
     * @param worldIn
     * @return
     */
    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        return false;
    }

    public boolean matchCraftingGrid(InventoryCrafting inv, World worldIn) {
        return super.matches(inv, worldIn);
    }

    /**
     * Matches current crafting result
     *
     * @param inv
     * @param world
     * @return
     */
    public boolean matchesWithoutGrid(IForgeInventory inv, World world) {
        return checkDna(inv, world)
                && checkLevel(inv, world)
                && checkExp(inv, world)
                && checkCatalysts(inv, world);
    }

    public boolean checkExp(IForgeInventory inv, World world) {
        if (experience > 0) {
            EntityPlayer player = inv.getCraftingPlayer();
            return player != null && player.experienceLevel >= experience;
        }

        return true;
    }

    public boolean checkLevel(IForgeInventory inv, World world) {
        return inv.getCurrentLevel() >= level;
    }

    public boolean checkDna(IForgeInventory inv, World world) {
        return inv.getCurrentDna().getFluidAmount() >= dna;
    }

    /**
     * Checks catalysts for current recipe
     *
     * @param inv
     * @param world
     * @return
     */
    public boolean checkCatalysts(IForgeInventory inv, World world) {
        if (ingredients.isEmpty())
            return true;

        List<ItemStack> inputs = inv.findCatalystStands().values().stream().flatMap(InventoryHelper::asStream).collect(Collectors.toList());
        return net.minecraftforge.common.util.RecipeMatcher.findMatches(inputs, this.ingredients) != null;
    }

    /**
     * Returns remaining items for crafting grid
     *
     * @param inv
     * @return
     */
    public NonNullList<ItemStack> getRemainingItemsFromCraftingGrid(IForgeInventory inv) {
        IItemHandler handler = inv.getCurrentHandler();

        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getHeight() * inv.getWidth(), ItemStack.EMPTY);

        for (int i = 0; i < nonnulllist.size(); ++i) {
            final ItemStack originalStack = handler.getStackInSlot(i);
            ItemStack itemstack = handler.getStackInSlot(i).copy();
            if (remaining.stream().noneMatch(x -> x.apply(originalStack))) {
                itemstack.shrink(1);

                if (itemstack.isEmpty()) {
                    itemstack = net.minecraftforge.common.ForgeHooks.getContainerItem(originalStack);
                }
            }

            nonnulllist.set(i, itemstack);
        }

        return nonnulllist;
    }

    /**
     * Returns remaining items for catalysts
     *
     * @param inv
     * @return
     */
    public Map<TileEntity, NonNullList<ItemStack>> getRemainingItemsForCatalysts(IForgeInventory inv) {
        HashMap<TileEntity, NonNullList<ItemStack>> map = new HashMap<>();

        Map<TileEntity, IItemHandler> catalystStands = inv.findCatalystStands();

        List<NbtIngredient> ingredientsCopy = new ArrayList<>(ingredients);

        for (Map.Entry<TileEntity, IItemHandler> entry : catalystStands.entrySet()) {
            IItemHandler value = entry.getValue();
            NonNullList<ItemStack> list = NonNullList.from(ItemStack.EMPTY, InventoryHelper.asStream(value).toArray(ItemStack[]::new));

            for (int i = 0; i < list.size(); i++) {
                ItemStack itemStack = list.get(i);
                NbtIngredient ingredient = ingredientsCopy.stream().filter(x -> x.apply(itemStack)).findFirst().orElse(null);
                if (ingredient == null)
                    continue;

                ItemStack containerItem = ForgeHooks.getContainerItem(itemStack);
                itemStack.shrink(ingredient.getStack().getCount());

                if (itemStack.isEmpty()) {
                    list.set(i, containerItem);
                }

                ingredientsCopy.remove(ingredient);
            }

            map.put(entry.getKey(), list);
        }

        return map;
        //List<ItemStack> inputs = inv.findCatalystStands().values().stream().flatMap(InventoryHelper::asStream).collect(Collectors.toList());

//        IItemHandler handler = inv.getCurrentHandler();
//
//        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getHeight() * inv.getWidth(), ItemStack.EMPTY);
//
//        for (int i = 0; i < nonnulllist.size(); ++i) {
//            ItemStack itemstack = handler.getStackInSlot(i);
//            boolean isRemaining = remaining.stream().anyMatch(x -> x.apply(itemstack));
//            nonnulllist.set(i, isRemaining ? net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack) : itemstack.copy());
//        }
//
//        return nonnulllist;
    }

    public ItemStack getCraftingResult(IForgeInventory inventory) {
        return getRecipeOutput().copy();
    }

    public int getExperience() {
        return experience;
    }

    public int getDna() {
        return dna;
    }

    public int getKnowledgeLevel() {
        return level;
    }

    public NonNullList<NbtIngredient> getCatalystIngredients() {
        return ingredients;
    }
}
