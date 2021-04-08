package divineadditions.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ItemMod extends Item {
    private final List<TextComponentTranslation> tooltip = new ArrayList<>();
    private final List<Item> repair = new ArrayList<>();

    private boolean immortal = false;

    public static Entity createImmortalEntity(World world, Entity location, ItemStack itemstack) {
        if (location instanceof EntityItem) {
            EntityItem entityItem = (EntityItem) location;

            entityItem.setNoDespawn();
            entityItem.setDefaultPickupDelay();
            entityItem.setEntityInvulnerable(true);
        }

        return null;
    }

    /**
     * Add info to current item
     *
     * @param translationKeys
     * @return
     */
    public ItemMod withInfo(String... translationKeys) {
        if (translationKeys != null && translationKeys.length > 0) {
            tooltip.addAll(Arrays.stream(translationKeys).map(x -> new TextComponentTranslation(x)).collect(Collectors.toList()));
        }

        return this;
    }

    /**
     * Creates item with immortial drop
     *
     * @return
     */
    public ItemMod immortal() {
        immortal = true;
        return withInfo("divineadditions.tooltip.immortal_item");
    }

    /**
     * Makes item accept passed items as repair ones
     *
     * @param repairItems
     * @return
     */
    public ItemMod repair(Item... repairItems) {
        if (repairItems != null && repairItems.length > 0) {
            repair.addAll(Arrays.stream(repairItems).collect(Collectors.toList()));
        }

        return this;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return this.repair.contains(repair.getItem());
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return immortal;
    }

    @Nullable
    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        return createImmortalEntity(world, location, itemstack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        this.tooltip.forEach(x -> tooltip.add(x.getFormattedText()));

        if (!repair.isEmpty() && net.minecraft.client.gui.GuiScreen.isShiftKeyDown()) {
            tooltip.add(new TextComponentString("").getFormattedText());
            tooltip.add(new TextComponentTranslation("divineadditions.tooltip.rifle.repair").getFormattedText());
            tooltip.addAll(repair.stream().map(x -> x.getDefaultInstance().getDisplayName()).collect(Collectors.toList()));
        }
    }
}
