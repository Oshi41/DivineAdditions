package divineadditions.utils;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.BlockRotationProcessor;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;

import javax.annotation.Nullable;

public class SkippingTemplateProcessor extends BlockRotationProcessor {
    public SkippingTemplateProcessor(BlockPos pos, PlacementSettings settings) {
        super(pos, settings);
    }

    @Nullable
    @Override
    public Template.BlockInfo processBlock(World worldIn, BlockPos pos, Template.BlockInfo blockInfoIn) {
        return blockInfoIn.blockState.getBlockHardness(worldIn, pos) > 0
                ? super.processBlock(worldIn, pos, blockInfoIn)
                : blockInfoIn;
    }
}
