package divineadditions.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Random;

@SideOnly(Side.CLIENT)
public class RenderEntityItemEnhanced extends Render<EntityItem> {
    private final RenderItem itemRenderer;
    private final Random random = new Random();
    private float scale = 1;

    public RenderEntityItemEnhanced(RenderManager renderManagerIn, RenderItem renderItem, float scale) {
        super(renderManagerIn);
        itemRenderer = renderItem;
        this.scale = scale;
    }

    @Override
    public void doRender(EntityItem entity, double x, double y, double z, float entityYaw, float partialTicks) {
        ItemStack itemStack = entity.getItem();

        if (itemStack.isEmpty())
            return;

        this.random.setSeed(Item.getIdFromItem(itemStack.getItem()) + itemStack.getMetadata());

        for (int i = 0; i < getModelCount(itemStack); i++) {
            GlStateManager.pushMatrix();

            float f1 = shouldBob()
                    ? MathHelper.sin(((float) entity.getAge() + partialTicks) / 10.0F + entity.hoverStart) * 0.1F + 0.1F
                    : 0;
            GlStateManager.translate(x, y + f1, z);

            float f3 = (((float) entity.getAge() + partialTicks) / 20.0F + entity.hoverStart) * (180F / (float) Math.PI);
            GlStateManager.rotate(f3, 0.0F, 1.0F, 0.0F);

            GlStateManager.scale(scale, scale, scale);

            if (i > 0) {
                float f7 = shouldSpreadItems() ? (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F : 0;
                float f9 = shouldSpreadItems() ? (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F : 0;
                float f6 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                GlStateManager.translate(f7, f9, f6);
            }

            IBakedModel ibakedmodel = itemRenderer.getItemModelWithOverrides(itemStack, entity.world, null);
            IBakedModel transformedModel = ForgeHooksClient.handleCameraTransforms(ibakedmodel, ItemCameraTransforms.TransformType.GROUND, false);

            itemRenderer.renderItem(itemStack, transformedModel);
            GlStateManager.popMatrix();
        }
    }

    protected boolean shouldSpreadItems() {
        return true;
    }

    protected boolean shouldBob() {
        return true;
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityItem entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    protected int getModelCount(ItemStack stack) {
        return Math.max(1, (int) (Math.log(stack.getCount()) / Math.log(2)));
    }
}
