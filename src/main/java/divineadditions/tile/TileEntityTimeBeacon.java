package divineadditions.tile;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.template.Template;

import java.util.HashMap;
import java.util.Map;

public class TileEntityTimeBeacon extends TileEntity {

    public boolean checkStructure(WorldServer world, BlockPos pos, ResourceLocation structureId) {
        if (world == null || pos == null || structureId == null)
            return false;

        Template template = world.getStructureTemplateManager().getTemplate(world.getMinecraftServer(), structureId);
        if (template == null)
            return false;


        BiMap<Integer, IBlockState> palette = HashBiMap.create();
        Map<BlockPos, IBlockState> blocks = new HashMap<>();

        return true;
    }

}
