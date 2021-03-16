package divineadditions.render;

import divineadditions.api.IEntityCage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class RenderModItemStack extends TileEntityItemStackRenderer {
    private final Map<ResourceLocation, Entity> entityMap = new HashMap<>();
    private final ItemStack defaultStack = Items.GLASS_BOTTLE.getDefaultInstance();

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        if (stack.getItem() instanceof IEntityCage) {
            ResourceLocation entityId = ((IEntityCage) stack.getItem()).getContainingEntityId(stack.getTagCompound());
            if (entityId != null) {
                Entity entity = entityMap.computeIfAbsent(entityId, location -> EntityList.createEntityByIDFromName(location, Minecraft.getMinecraft().world));
                if (entity != null) {
                    Render render = Minecraft.getMinecraft().getRenderManager().entityRenderMap.get(entity.getClass());
                    if (render != null) {
                        render.doRender(entity, 0, 0, 0, 0, partialTicks);
                        return;
                    }
                }
            }
        }


        super.renderByItem(defaultStack, partialTicks);
    }
}
