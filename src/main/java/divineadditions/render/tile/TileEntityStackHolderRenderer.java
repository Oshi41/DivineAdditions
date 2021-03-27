package divineadditions.render.tile;

import divineadditions.render.entity.RenderEntityItemEnhanced;
import divinerpg.utils.Lazy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import openmods.utils.InventoryUtils;

@SideOnly(Side.CLIENT)
public class TileEntityStackHolderRenderer extends TileEntitySpecialRenderer<TileEntity> {
    private final Lazy<EntityItem> inner = new Lazy<>(() -> new EntityItem(Minecraft.getMinecraft().world));
    private final Lazy<RenderEntityItem> renderer = new Lazy<>(() -> new RenderEntityItemEnhanced(Minecraft.getMinecraft().getRenderManager(), Minecraft.getMinecraft().getRenderItem()));


    @Override
    public void render(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        IItemHandler itemHandler = InventoryUtils.tryGetHandler(te, null);
        if (itemHandler != null) {
            ItemStack itemStack = itemHandler.getStackInSlot(0);

            if (!itemStack.isEmpty()) {

                AxisAlignedBB boundingBox = te.getWorld().getBlockState(te.getPos()).getBoundingBox(te.getWorld(), te.getPos());

                double xPos = x + 0.5;
                double yPos = y + boundingBox.maxY;
                double zPos = z + 0.5;

                inner.getValue().setItem(itemStack);

                GlStateManager.pushMatrix();
                GlStateManager.disableLighting();
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 15 * 16, 15 * 16);
                long totalWorldTime = getWorld().getTotalWorldTime();

                inner.getValue().turn(totalWorldTime, 0);

                renderer.getValue().doRender(inner.getValue(), xPos, yPos, zPos, 0, (float) Math.sin(totalWorldTime / 360.0) * 360);

                GlStateManager.enableLighting();
                GlStateManager.popMatrix();
            }
        }

        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
    }
}
