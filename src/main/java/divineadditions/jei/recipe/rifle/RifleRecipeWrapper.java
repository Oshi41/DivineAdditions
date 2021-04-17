package divineadditions.jei.recipe.rifle;

import divineadditions.api.IRifleCoreConfig;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.ITooltipCallback;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RifleRecipeWrapper implements IRecipeWrapper, ITooltipCallback<ItemStack> {
    private final ItemStack core;
    private final List<ItemStack> bullets;
    private final List<ItemStack> catalysts;


    public RifleRecipeWrapper(IGuiHelper helper, Item core, IRifleCoreConfig coreConfig) {
        this.core = new ItemStack(core);

        bullets = coreConfig
                .getBullets()
                .entrySet()
                .stream()
                .map(x -> new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(x.getKey())), x.getValue()))
                .filter(x -> !x.isEmpty())
                .collect(Collectors.toList());

        catalysts = coreConfig
                .getCatalysts()
                .entrySet()
                .stream()
                .map(x -> new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(x.getKey())), x.getValue()))
                .filter(x -> !x.isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

    }

    @Override
    public void getIngredients(IIngredients iIngredients) {
        List<List<ItemStack>> imputs = new ArrayList<>();
        imputs.add(Collections.singletonList(core));
        imputs.add(1, bullets);
        imputs.add(1, catalysts);
        iIngredients.setInputLists(VanillaTypes.ITEM, imputs);
    }

    @Override
    public void onTooltip(int slotIndex, boolean input, ItemStack ingredient, List<String> tooltip) {
        switch (slotIndex) {
            case 0:
                tooltip.add("");
                tooltip.add(new TextComponentTranslation("divineadditions.jei.rifle_core.insert").getFormattedText());
                break;

            case 1:
                tooltip.add("");
                tooltip.add(new TextComponentTranslation("divineadditions.jei.rifle_core.catalyst").getFormattedText());
                break;

            case 2:
                tooltip.add("");
                tooltip.add(new TextComponentTranslation("divineadditions.jei.rifle_core.bullets").getFormattedText());
                break;
        }
    }
}
