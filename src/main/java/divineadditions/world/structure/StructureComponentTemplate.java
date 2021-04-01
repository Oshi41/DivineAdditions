package divineadditions.world.structure;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;

import java.util.Random;

public class StructureComponentTemplate extends net.minecraft.world.gen.structure.StructureComponentTemplate {

    public StructureComponentTemplate(Template template, BlockPos pos, PlacementSettings settings) {
        if (template != null)
            setup(template, pos, settings);
    }

    @Override
    protected void handleDataMarker(String function, BlockPos pos, World worldIn, Random rand, StructureBoundingBox sbb) {
        // ignore
    }
}
