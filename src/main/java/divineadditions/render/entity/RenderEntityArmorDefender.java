package divineadditions.render.entity;

import divineadditions.entity.EntityArmorDefender;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderEntityArmorDefender extends RenderBiped<EntityArmorDefender> {

    public RenderEntityArmorDefender(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelPlayer(0.0F, false), 0.5F);
        this.addLayer(new LayerBipedArmor(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityArmorDefender entity) {
        EntityPlayer summoner = entity.getSummoner();
        if (summoner instanceof AbstractClientPlayer) {
            return ((AbstractClientPlayer) summoner).getLocationSkin();
        }

        return super.getEntityTexture(entity);
    }
}
