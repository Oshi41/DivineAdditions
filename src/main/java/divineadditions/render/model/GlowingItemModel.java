package divineadditions.render.model;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.List;

public class GlowingItemModel implements IBakedModel {

    private IBakedModel bakedModel;

    public GlowingItemModel() {

    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        return getBakedModel().getQuads(state, side, rand);
    }

    @Override
    public boolean isAmbientOcclusion() {
        return getBakedModel().isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return getBakedModel().isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return getBakedModel().isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return getBakedModel().getParticleTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return getBakedModel().getItemCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return getBakedModel().getOverrides();
    }

    @Override
    public boolean isAmbientOcclusion(IBlockState state) {
        return getBakedModel().isAmbientOcclusion(state);
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        return getBakedModel().handlePerspective(cameraTransformType);
    }

    public IBakedModel getBakedModel() {
        return bakedModel;
    }

    public void setBakedModel(IBakedModel bakedModel) {
        this.bakedModel = bakedModel;
    }
}
