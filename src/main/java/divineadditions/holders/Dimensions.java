package divineadditions.holders;

import divineadditions.DivineAdditions;
import divineadditions.config.DivineAdditionsConfig;
import divineadditions.world.dimension.planet.PlanetWorldProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.DimensionManager;

public class Dimensions {
    public static final DimensionType planetDimension = create("planets", DivineAdditionsConfig.planetDimensionConfig.id, PlanetWorldProvider.class);


    public static void register() {
        registerSingle(planetDimension);
    }

    private static DimensionType create(String name, int id, Class<? extends WorldProvider> klass) {
        ResourceLocation location = new ResourceLocation(DivineAdditions.MOD_ID, name);
        return DimensionType.register(location.toString(), "_" + name, id, klass, false);
    }

    private static void registerSingle(DimensionType type) {
        DimensionManager.registerDimension(type.getId(), type);
    }
}
