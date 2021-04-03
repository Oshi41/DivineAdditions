package divineadditions.render.entity;

import divineadditions.api.ISkinProvider;
import net.minecraft.client.model.ModelVillager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class RenderEntityAncientVillager<T extends EntityLiving & ISkinProvider> extends RenderLiving<T> {

    public RenderEntityAncientVillager(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new ModelVillager(0.0F), 0.5F);
        this.addLayer(new LayerCustomHead(this.getMainModel().villagerHead));
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return entity.getSkin();
    }

    @Override
    public ModelVillager getMainModel() {
        return (ModelVillager) super.getMainModel();
    }

}
