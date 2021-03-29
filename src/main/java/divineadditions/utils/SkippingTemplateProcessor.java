package divineadditions.utils;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.template.Template;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class SkippingTemplateProcessor implements ITemplateProcessor {

    private final Random random;
    private final float integrityPercentage;
    private final Set<Block> shouldNoReplaced = new HashSet<>();

    /**
     * Ctor
     *
     * @param random              - random variable
     * @param integrityPercentage - integrity for structure [0-1]. 0.8 means 80% of block will present but 20% is missing from structure
     */
    public SkippingTemplateProcessor(Random random, float integrityPercentage) {
        this.random = random;
        this.integrityPercentage = MathHelper.clamp(integrityPercentage, 0, 1);
    }

    public SkippingTemplateProcessor noReplace(Block... blocks) {
        shouldNoReplaced.addAll(Arrays.stream(blocks).collect(Collectors.toList()));
        return this;
    }

    @Nullable
    @Override
    public Template.BlockInfo processBlock(World worldIn, BlockPos pos, Template.BlockInfo blockInfoIn) {
        if (!blockInfoIn.blockState.getBlock().isAir(blockInfoIn.blockState, worldIn, pos)
                && !shouldNoReplaced.contains(blockInfoIn.blockState.getBlock())
                && integrityPercentage < 1) {
            if (random.nextFloat() > integrityPercentage) {
                return new Template.BlockInfo(pos, Blocks.AIR.getDefaultState(), null);
            }
        }

        return blockInfoIn;
    }
}
