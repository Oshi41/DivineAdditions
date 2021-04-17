package divineadditions.jei;

import com.google.common.collect.Lists;
import divineadditions.DivineAdditions;
import divineadditions.api.IEntityCage;
import divineadditions.gui.conainter.ForgeContainer;
import divineadditions.gui.conainter.PotionFurnaceContainer;
import divineadditions.gui.gui_container.ForgeGuiContainer;
import divineadditions.gui.gui_container.PotionFurnaceGuiContainer;
import divineadditions.holders.Blocks;
import divineadditions.holders.Dimensions;
import divineadditions.holders.Items;
import divineadditions.item.sword.ItemCustomSword;
import divineadditions.jei.category.ForgeRecipeCategory;
import divineadditions.jei.category.PotionFurnaceCategory;
import divineadditions.jei.category.RifleCoreCategory;
import divineadditions.jei.category.SoulSwordCategory;
import divineadditions.jei.recipe.ForgeRecipeWrapper;
import divineadditions.jei.recipe.PotionFurnaceRecipeWrapper;
import divineadditions.jei.recipe.SoulSwordRecipeWrapper;
import divineadditions.jei.recipe.rifle.RifleMobRecipeWrapper;
import divineadditions.recipe.ForgeRecipes;
import divinerpg.registry.ItemRegistry;
import mezz.jei.api.*;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.*;
import java.util.stream.Collectors;

@JEIPlugin
public class JeiModule implements IModPlugin {
    /**
     * Registering special item info
     *
     * @param registry
     */
    private static void registerInfos(IModRegistry registry) {
        registry.addIngredientInfo(new ItemStack(Blocks.time_beacon), VanillaTypes.ITEM,
                "divineadditions.jei.time_beacon");

        registry.addIngredientInfo(new ItemStack(Items.time_drop), VanillaTypes.ITEM,
                "divineadditions.jei.time_drop");

        ItemStack starSack = new ItemStack(ItemRegistry.teleportationStar);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("Dim", Dimensions.planetDimension.getName());
        tag.setLong("BlockPos", new BlockPos(0, 200, 0).toLong());
        starSack.setItemDamage(starSack.getMaxDamage() - 1);
        starSack.setTagCompound(tag);

        registry.addIngredientInfo(starSack, VanillaTypes.ITEM,
                "divineadditions.jei.teleportation_star");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();

        registry.addRecipeCategories(new ForgeRecipeCategory(guiHelper), new SoulSwordCategory(guiHelper), new RifleCoreCategory(guiHelper), new PotionFurnaceCategory(guiHelper));
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
        subtypeRegistry.registerSubtypeInterpreter(Items.caged_mob, itemStack -> itemStack.getOrCreateSubCompound(IEntityCage.cagedTagName).getString(IEntityCage.entityIdName));
        subtypeRegistry.useNbtForSubtypes(Items.soul_sword, Items.rifle_mob_core, ItemRegistry.teleportationStar);

        ForgeRegistries.ITEMS.getValuesCollection().stream().filter(x -> x instanceof ItemCustomSword).forEach(subtypeRegistry::useNbtForSubtypes);
    }

    /**
     * Register anvil recipes. JEI isn't really correct about it
     */
    private static void registerAnvil(IModRegistry registry, IIngredientRegistry ingredientRegistry) {
        List<ItemStack> stacks = ingredientRegistry.getAllIngredients(VanillaTypes.ITEM)
                .stream()
                // only for my mod
                .filter(x -> x.getItem().getRegistryName().getResourceDomain().equals(DivineAdditions.MOD_ID))
                .filter(ItemStack::isItemEnchantable)
                .collect(Collectors.toList());
        Collection<Enchantment> enchantments = ForgeRegistries.ENCHANTMENTS.getValuesCollection();

        for (ItemStack stack : stacks) {

            if (enchantments.stream().noneMatch(x -> x.canApplyAtEnchantingTable(stack))) {
                Enchantment enchantment = enchantments.stream().filter(x -> stack.getItem().canApplyAtEnchantingTable(stack, x)).findFirst().orElse(null);

                if (enchantment != null) {
                    List<IRecipeWrapper> recipes = getBookEnchantmentRecipes(registry.getJeiHelpers().getVanillaRecipeFactory(), enchantment, stack);
                    registry.addRecipes(recipes, VanillaRecipeCategoryUid.ANVIL);
                }
            }
        }
    }

    private static List<IRecipeWrapper> getBookEnchantmentRecipes(IVanillaRecipeFactory vanillaRecipeFactory, Enchantment enchantment, ItemStack ingredient) {
        Item item = ingredient.getItem();
        List<ItemStack> perLevelBooks = Lists.newArrayList();
        List<ItemStack> perLevelOutputs = Lists.newArrayList();
        for (int level = 1; level <= enchantment.getMaxLevel(); level++) {
            Map<Enchantment, Integer> enchMap = Collections.singletonMap(enchantment, level);

            ItemStack bookEnchant = new ItemStack(net.minecraft.init.Items.ENCHANTED_BOOK);
            EnchantmentHelper.setEnchantments(enchMap, bookEnchant);
            if (item.isBookEnchantable(ingredient, bookEnchant)) {
                perLevelBooks.add(bookEnchant);

                ItemStack withEnchant = ingredient.copy();
                EnchantmentHelper.setEnchantments(enchMap, withEnchant);
                perLevelOutputs.add(withEnchant);
            }
        }

        List<IRecipeWrapper> recipes = new ArrayList<>();

        if (!perLevelBooks.isEmpty() && !perLevelOutputs.isEmpty()) {
            IRecipeWrapper anvilRecipe = vanillaRecipeFactory.createAnvilRecipe(ingredient, perLevelBooks, perLevelOutputs);
            recipes.add(anvilRecipe);
        }

        return recipes;
    }

    @Override
    public void register(IModRegistry registry) {
        registerInfos(registry);
        registerAnvil(registry, registry.getIngredientRegistry());

        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        IStackHelper stackHelper = registry.getJeiHelpers().getStackHelper();

        registry.addRecipes(Arrays.asList(new SoulSwordRecipeWrapper()), SoulSwordCategory.ID);
        registry.addRecipes(Arrays.asList(new RifleMobRecipeWrapper(guiHelper)), RifleCoreCategory.ID);
        registry.addRecipes(PotionFurnaceRecipeWrapper.getResults(guiHelper, stackHelper), PotionFurnaceCategory.ID);

        List<ForgeRecipes> recipesList = ForgeRegistries
                .RECIPES
                .getValuesCollection()
                .stream()
                .filter(x -> x instanceof ForgeRecipes)
                .map(x -> ((ForgeRecipes) x))
                .collect(Collectors.toList());

        registry.addRecipes(recipesList.stream().map(x -> new ForgeRecipeWrapper(x, guiHelper))
                .collect(Collectors.toList()), ForgeRecipeCategory.ID.toString());


        registry.getRecipeTransferRegistry().addRecipeTransferHandler(ForgeContainer.class, ForgeRecipeCategory.ID.toString(), 0, 25, 27, 9 * 4);
        registry.addRecipeClickArea(ForgeGuiContainer.class, 121, 46, 28, 22, ForgeRecipeCategory.ID.toString());


        registry.getRecipeTransferRegistry().addRecipeTransferHandler(PotionFurnaceContainer.class, PotionFurnaceCategory.ID, 0, 3, 4, 9 * 4);
        registry.addRecipeClickArea(PotionFurnaceGuiContainer.class, 139, 25, 32, 24, PotionFurnaceCategory.ID);

    }
}
