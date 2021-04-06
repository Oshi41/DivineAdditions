package divineadditions.render.entity;

import divineadditions.DivineAdditions;
import divineadditions.entity.EntityDefenderStand;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Arrays;

@SideOnly(Side.CLIENT)
public class RenderEntityDefenderStand extends RenderLivingBase<EntityDefenderStand> {
    private static final ResourceLocation DEFAULT_RES_LOC = new ResourceLocation("textures/entity/steve.png");
    private final LayerBipedArmor layerbipedarmor;
    private Field alphaField;
    private float currentAlpha;

    public RenderEntityDefenderStand(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelPlayer(0.0F, false), 0.5F);
        layerbipedarmor = new LayerBipedArmor(this);
        this.addLayer(layerbipedarmor);
        this.addLayer(new LayerHeldItem(this) {
            @Override
            public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, currentAlpha);
                GlStateManager.depthMask(false);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                GlStateManager.alphaFunc(516, 0.003921569F);

                super.doRenderLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);

                GlStateManager.disableBlend();
                GlStateManager.alphaFunc(516, 0.1F);
                GlStateManager.depthMask(true);
            }
        });

        try {
            Field field = ObfuscationReflectionHelper.findField(LayerArmorBase.class, "alpha");
            field.setAccessible(true);
            field.set(layerbipedarmor, 0.15f);

            alphaField = field;
        } catch (Exception e) {
            DivineAdditions.logger.warn(e);
        }
    }

    @Override
    public void doRender(EntityDefenderStand entity, double x, double y, double z, float entityYaw, float partialTicks) {
        long count = Arrays.stream(EntityEquipmentSlot.values()).mapToInt(slot -> entity.getItemStackFromSlot(slot).isEmpty() ? 0 : 1).sum();
        currentAlpha = 0.3f + (count / 10f);

        try {
            if (alphaField != null)
                alphaField.set(layerbipedarmor, currentAlpha);
        } catch (IllegalAccessException e) {
            DivineAdditions.logger.warn(e);
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, currentAlpha);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.alphaFunc(516, 0.003921569F);

        super.doRender(entity, x, y, z, entityYaw, partialTicks);

        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.depthMask(true);
    }

    @Override
    protected void renderEntityName(EntityDefenderStand entityIn, double x, double y, double z, String name, double distanceSq) {
        return;
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityDefenderStand entity) {
        if (entity.getOwner() instanceof AbstractClientPlayer) {
            ((AbstractClientPlayer) entity.getOwner()).getLocationSkin();
        }

        return DEFAULT_RES_LOC;
    }
}
