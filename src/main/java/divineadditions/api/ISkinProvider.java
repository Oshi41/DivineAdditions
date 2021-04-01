package divineadditions.api;

import net.minecraft.util.ResourceLocation;

/**
 * Provider for renderers
 */
public interface ISkinProvider {

    /**
     * Returns current entity skin
     *
     * @return
     */
    ResourceLocation getSkin();

    /**
     * Set curren entity skin
     *
     * @param id
     */
    void setSkin(ResourceLocation id);
}
