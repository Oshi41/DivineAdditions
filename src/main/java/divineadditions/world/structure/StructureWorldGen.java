package divineadditions.world.structure;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.template.TemplateManager;

import java.util.Random;

public class StructureWorldGen extends StructureComponent {
    private final WorldGenerator generator;

    public StructureWorldGen(WorldGenerator generator, BlockPos pos) {
        this.generator = generator;
        boundingBox = new StructureBoundingBox(pos.getX() - 16, pos.getY() - 16, pos.getX() + 16, pos.getZ() + 16);
    }

    @Override
    protected void writeStructureToNBT(NBTTagCompound tagCompound) {

    }

    @Override
    protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager p_143011_2_) {

    }

    @Override
    public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
        BlockPos chunkStart = new BlockPos(structureBoundingBoxIn.minX, structureBoundingBoxIn.minY, structureBoundingBoxIn.minZ);
        return generator.generate(worldIn, randomIn, chunkStart);
    }
}
