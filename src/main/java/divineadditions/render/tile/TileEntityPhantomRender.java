package divineadditions.render.tile;

import divineadditions.api.IPhantomRender;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Map;

public class TileEntityPhantomRender<T extends TileEntity & IPhantomRender> extends TileEntitySpecialRenderer<T> {

    @Override
    public void render(T te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;

        Map<BlockPos, IBlockState> phantomBlocks = te.getPhantomBlocks();
        if (phantomBlocks == null || phantomBlocks.isEmpty())
            return;

        for (Map.Entry<BlockPos, IBlockState> entry : phantomBlocks.entrySet()) {
            IBlockState stateToRender = entry.getValue();
            Vec3d blockpos = new Vec3d(entry.getKey());
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            GlStateManager.pushMatrix();

            GlStateManager.disableLighting();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) partialTicks;
            double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) partialTicks;
            double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) partialTicks;
            buffer.setTranslation(-d0, -d1, -d2);
            buffer.begin(7, DefaultVertexFormats.BLOCK);
            GlStateManager.color(1, 1, 1, 0.7f);
            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
            blockrendererdispatcher.getBlockModelRenderer().renderModel(te.getWorld(), blockrendererdispatcher.getModelForState(stateToRender),
                    stateToRender,
                    new BlockPos(blockpos),
                    buffer,
                    false,
                    MathHelper.getCoordinateRandom(((int) blockpos.x), (int) blockpos.y, (int) blockpos.z));
            tessellator.draw();
            buffer.setTranslation(0, 0, 0);

            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
    }
}