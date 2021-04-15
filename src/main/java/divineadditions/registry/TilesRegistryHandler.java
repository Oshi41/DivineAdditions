package divineadditions.registry;

import divineadditions.DivineAdditions;
import divineadditions.tile.TileEntityCatalystStand;
import divineadditions.tile.TileEntityForge;
import divineadditions.tile.TileEntityPotionFurnace;
import divineadditions.tile.TileEntityTimeBeacon;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TilesRegistryHandler {

    public static void register() {
        registerTile(TileEntityCatalystStand.class, "catalyst_stand");
        registerTile(TileEntityTimeBeacon.class, "time_beacon");
        registerTile(TileEntityForge.class, "forge");
        registerTile(TileEntityPotionFurnace.class, "potion_furnace");
    }

    private static void registerTile(Class<? extends TileEntity> klass, String name) {
        GameRegistry.registerTileEntity(klass, new ResourceLocation(DivineAdditions.MOD_ID, name));
    }
}
