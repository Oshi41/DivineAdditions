package divineadditions.registry;

import divineadditions.DivineAdditions;
import divineadditions.tile.TileEntityStackHolder;
import divineadditions.tile.TileEntitySummoningAltar;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TilesRegistryHandler {

    public static void register() {
        registerTile(TileEntityStackHolder.class, "stack_holder");
        registerTile(TileEntitySummoningAltar.class, "summoning_altar");
    }

    private static void registerTile(Class<? extends TileEntity> klass, String name) {
        GameRegistry.registerTileEntity(klass, new ResourceLocation(DivineAdditions.MOD_ID, name));
    }
}
