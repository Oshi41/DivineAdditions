package divineadditions.registry;

import divineadditions.DivineAdditions;
import divineadditions.tile.TileEntityCatalystStand;
import divineadditions.tile.TileEntityInfusingAltar;
import divineadditions.tile.TileEntitySummoningAltar;
import divineadditions.tile.TileEntityTimeBeacon;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TilesRegistryHandler {

    public static void register() {
        registerTile(TileEntityCatalystStand.class, "stack_holder");
        registerTile(TileEntitySummoningAltar.class, "summoning_platform");
        registerTile(TileEntityInfusingAltar.class, "infusing_altar");
        registerTile(TileEntityTimeBeacon.class, "time_beacon");
    }

    private static void registerTile(Class<? extends TileEntity> klass, String name) {
        GameRegistry.registerTileEntity(klass, new ResourceLocation(DivineAdditions.MOD_ID, name));
    }
}
