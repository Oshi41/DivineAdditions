package divineadditions.world.dimension.planet;

import divineadditions.config.DivineAdditionsConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureStart;

import javax.annotation.Nullable;

public class AncientPortalStructure extends MapGenStructure {
    @Override
    public String getStructureName() {
        return "ancient_portal";
    }

    @Nullable
    @Override
    public BlockPos getNearestStructurePos(World worldIn, BlockPos pos, boolean findUnexplored) {
        this.world = worldIn;
        return findNearestStructurePosBySpacing(worldIn, this, pos, 20, 11, 10387313, true, 100, findUnexplored);
    }

    @Override
    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
        return world.rand.nextInt(DivineAdditionsConfig.planetDimensionConfig.portalSpawnChance) == 0;
    }

    @Override
    protected StructureStart getStructureStart(int chunkX, int chunkZ) {
        return new AncientPortalStart(world, chunkX, chunkZ);
    }
}
