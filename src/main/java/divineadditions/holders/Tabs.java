package divineadditions.holders;

import divineadditions.DivineAdditions;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class Tabs {
    public static final CreativeTabs Main = new CreativeTabs(DivineAdditions.MOD_ID) {
        @Override
        public ItemStack getTabIconItem() {
            return Items.rifle_mob_core.getDefaultInstance();
        }
    };
}
