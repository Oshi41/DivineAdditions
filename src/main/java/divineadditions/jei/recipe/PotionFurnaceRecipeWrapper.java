package divineadditions.jei.recipe;

import divineadditions.config.DivineAdditionsConfig;
import divineadditions.item.sword.ItemCustomSword;
import divineadditions.item.sword.SwordProperties;
import divineadditions.jei.category.PotionFurnaceCategory;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.ITooltipCallback;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PotionFurnaceRecipeWrapper implements ITooltipCallback<ItemStack>, IRecipeWrapper {
    private static IDrawable fireAnimation;

    private final List<ItemStack> fuel;
    private final List<ItemStack> potions;
    private final ItemStack swords;
    private final List<ItemStack> results;

    protected PotionFurnaceRecipeWrapper(List<ItemStack> fuel, List<ItemStack> potions, ItemStack swords, List<ItemStack> results) {
        this.fuel = fuel;
        this.potions = potions;
        this.swords = swords;
        this.results = results;
    }

    public static List<PotionFurnaceRecipeWrapper> getResults(IGuiHelper helper, IStackHelper stackHelper) {
        // see PotionFurnaceGuiContainer.drawGuiContainerBackgroundLayer
        fireAnimation = helper.drawableBuilder(PotionFurnaceCategory.Background, 176, 0, 14, 14)
                .buildAnimated(100, IDrawableAnimated.StartDirection.BOTTOM, false);

        List<PotionFurnaceRecipeWrapper> result = new ArrayList<>();

        List<ItemStack> fuels = DivineAdditionsConfig
                .potionFurnaceConfig
                .potionFurnaceFuel
                .keySet()
                .stream()
                .map(x -> Item.REGISTRY.getObject(new ResourceLocation(x)))
                .filter(Objects::nonNull)
                .map(Item::getDefaultInstance)
                .collect(Collectors.toList());

        List<ItemStack> possiblePotions = ForgeRegistries
                .ITEMS
                .getValuesCollection()
                .stream()
                .filter(x -> x instanceof ItemPotion)
                .flatMap(x -> {
                    NonNullList<ItemStack> list = NonNullList.create();
                    x.getSubItems(CreativeTabs.BREWING, list);
                    list.addAll(stackHelper.getSubtypes(x.getDefaultInstance()));
                    return list.stream();
                })
                .distinct()
                .filter(x -> !PotionUtils.getEffectsFromStack(x).isEmpty())
                .collect(Collectors.toList());

        ForgeRegistries
                .ITEMS
                .getValuesCollection()
                .stream()
                .filter(x -> x instanceof ItemCustomSword)
                .forEach(item -> {
                    List<ItemStack> potions = new ArrayList<>();
                    ItemStack originalSword = item.getDefaultInstance();
                    List<ItemStack> results = new ArrayList<>();

                    SwordProperties props = ((ItemCustomSword) item).getSwordProps();
                    for (ItemStack potion : possiblePotions) {
                        ItemStack resultSword = originalSword.copy();

                        List<PotionEffect> effectsFromStack = PotionUtils.getEffectsFromStack(potion);
                        for (PotionEffect effect : effectsFromStack) {
                            props.addAttackEffect(resultSword, effect);
                        }

                        results.add(resultSword);
                        potions.add(potion);
                    }

                    result.add(new PotionFurnaceRecipeWrapper(fuels, potions, originalSword, results));
                });

        return result;
    }

    @Override
    public void onTooltip(int slot, boolean input, ItemStack stack, List<String> list) {
        ITextComponent text = null;

        switch (slot) {
            case 0:
            case 2:
                text = new TextComponentTranslation("divineadditions.jei.potion_furnace.same_item");
                break;

            case 4:
                text = new TextComponentTranslation("divineadditions.jei.potion_furnace.cauldron_needed");
                break;
        }

        if (text != null) {
            list.add("");
            list.add(text.getFormattedText());
        }
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        if (fireAnimation != null)
            fireAnimation.draw(minecraft, 149, 32);
    }

    @Override
    public void getIngredients(IIngredients iIngredients) {

        ArrayList<List<ItemStack>> list = new ArrayList<>();
        list.add(potions);
        list.add(Collections.singletonList(swords));
        list.add(potions);
        list.add(fuel);

        ArrayList<List<ItemStack>> output = new ArrayList<>();
        output.add(results);

        iIngredients.setInputLists(VanillaTypes.ITEM, list);
        iIngredients.setOutputLists(VanillaTypes.ITEM, output);
    }
}
