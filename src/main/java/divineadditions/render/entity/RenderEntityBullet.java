package divineadditions.render.entity;

import divineadditions.DivineAdditions;
import divinerpg.objects.entities.assets.render.RenderDivineProjectile;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class RenderEntityBullet extends RenderDivineProjectile<Entity> {
    private static final ResourceLocation texture = new ResourceLocation(DivineAdditions.MOD_ID, "textures/entity/bullet.png");

    public RenderEntityBullet(RenderManager manager) {
        super(manager, 1);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return texture;
    }
}
