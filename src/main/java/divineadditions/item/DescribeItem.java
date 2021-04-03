package divineadditions.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DescribeItem extends Item {
    private final List<TextComponentTranslation> tooltip;

    public DescribeItem(String... translationKeys) {
        if (translationKeys != null && translationKeys.length > 0) {
            tooltip = Arrays.stream(translationKeys).map(x -> new TextComponentTranslation(x)).collect(Collectors.toList());
        } else {
            tooltip = new ArrayList<>();
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        this.tooltip.forEach(x -> tooltip.add(x.getFormattedText()));
    }
}
