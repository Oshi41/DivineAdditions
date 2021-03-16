package divineadditions.render;

import divineadditions.api.ISingleItemHandler;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderItem<T extends Entity & ISingleItemHandler> extends RenderSnowball<T> {
    public RenderItem(RenderManager renderManagerIn, net.minecraft.client.renderer.RenderItem itemRendererIn) {
        super(renderManagerIn, Items.SNOWBALL, itemRendererIn);
    }

    @Override
    public ItemStack getStackToRender(T entityIn) {
        return entityIn.getItemStack();
    }
}
