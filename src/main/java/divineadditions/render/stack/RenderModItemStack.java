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
import org.lwjgl.opengl.GL11;

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
                        GlStateManager.pushMatrix();
                        float maxWidth = 1.8F;

                        float maxDimension = Math.max(entity.height, entity.width);

                        if (Math.abs(maxDimension - maxWidth) >= Double.MIN_VALUE) {
                            float scale = maxWidth / maxDimension;
                            GlStateManager.scale(scale, scale, scale);
                        }

                        GlStateManager.translate(0, 0, 0.7);

                        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                        render.doRender(entity, 0, 0, 0, 0, partialTicks);
                        GL11.glPopAttrib();
                        GlStateManager.popMatrix();
                        return;
                    }
                }
            }
        }


        super.renderByItem(defaultStack, partialTicks);
    }
}
