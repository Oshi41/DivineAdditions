package divineadditions.render.entity;

import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderEntityItemEnhanced extends RenderEntityItem {
    public RenderEntityItemEnhanced(RenderManager renderManagerIn, RenderItem renderItem) {
        super(renderManagerIn, renderItem);
    }

    @Override
    protected int getModelCount(ItemStack stack) {
        return Math.max(1, (int) (Math.log(stack.getCount()) / Math.log(2)));
    }
}
