package divineadditions.render.stack;

import divineadditions.api.IEntityCage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
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
                        float maxSize = Math.max(entity.width, entity.height);
                        if (maxSize > 2) {
                            float scale = 2 / (maxSize);
                            GlStateManager.scale(scale, scale, scale);
                            GlStateManager.translate(scale, 0, scale);
                        }

                        if (maxSize < 1.5f) {
                            float scale = 1.5f / maxSize;
                            GlStateManager.scale(scale, scale, scale);
                        }

                        GlStateManager.translate(0, 0, 0.7);
                        GlStateManager.disableLighting();
                        render.doRender(entity, 0, 0, 0, 0, partialTicks);
                        GlStateManager.enableLighting();
                        return;
                    }
                }
            }
        }


        super.renderByItem(defaultStack, partialTicks);
    }
}
