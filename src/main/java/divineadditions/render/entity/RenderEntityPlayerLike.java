package divineadditions.render.entity;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderEntityPlayerLike extends RenderBiped<AbstractSkeleton> {

    public RenderEntityPlayerLike(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelPlayer(0.0F, false), 0.5F);
        this.addLayer(new LayerBipedArmor(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(AbstractSkeleton entity) {
        if (entity.getAttackTarget() instanceof AbstractClientPlayer) {
            return ((AbstractClientPlayer) entity.getAttackTarget()).getLocationSkin();
        }

        return super.getEntityTexture(entity);
    }
}
