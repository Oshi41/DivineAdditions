package divineadditions.render.sky;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PlanetsSkyRender extends IRenderHandler {

    private static final ResourceLocation END_SKY_TEXTURES = new ResourceLocation("textures/environment/end_sky.png");

    @Override
    public void render(float partialTicks, WorldClient world, Minecraft mc) {
        GlStateManager.disableFog();
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.depthMask(false);
        mc.renderEngine.bindTexture(END_SKY_TEXTURES);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        for (int k1 = 0; k1 < 6; ++k1) {
            GlStateManager.pushMatrix();

            // north
            if (k1 == 1) {
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0);
            }

            // south
            if (k1 == 2) {
                GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            }

            // top
            if (k1 == 3) {
                GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
            }

            // east
            if (k1 == 4) {
                GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
            }

            // west
            if (k1 == 5) {
                GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
            }

            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

            double size = 100.0D;
            int color = 80;

            bufferbuilder.pos(-size, -size, -size).tex(0.0D, 0.0D).color(color, color, color, 255).endVertex();
            bufferbuilder.pos(-size, -size, size).tex(0.0D, 16.0D).color(color, color, color, 255).endVertex();
            bufferbuilder.pos(size, -size, size).tex(16.0D, 16.0D).color(color, color, color, 255).endVertex();
            bufferbuilder.pos(size, -size, -size).tex(16.0D, 0.0D).color(color, color, color, 255).endVertex();
            tessellator.draw();
            GlStateManager.popMatrix();
        }

        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
    }
}
