package divineadditions.utils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.block.state.pattern.BlockStateMatcher;
import net.minecraft.block.state.pattern.FactoryBlockPattern;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StructureUtils {
    private static final Map<ResourceLocation, StructureInfo> templates = new HashMap<>();

    public static boolean placeStructure(World world, BlockPos pos, ResourceLocation id, float integrity) {
        if (!(world instanceof WorldServer)) {
            return false;
        }

        StructureInfo structureInfo = readFromNbt((WorldServer) world, id);

        BlockPattern.PatternHelper match = structureInfo.match(world, pos);
        if (match != null)
            return true;

        Template template = ((WorldServer) world).getStructureTemplateManager().getTemplate(world.getMinecraftServer(), id);
        if (template == null)
            return false;

        template.addBlocksToWorld(world, pos, new SkippingTemplateProcessor(world.rand, integrity), new PlacementSettings(), 2);
        return true;
    }

    @Nullable
    public static StructureInfo readFromNbt(WorldServer world, ResourceLocation id) {
        StructureInfo structureInfo = templates.get(id);
        if (structureInfo != null)
            return structureInfo;

        BiMap<Integer, IBlockState> palette = HashBiMap.create();
        Map<BlockPos, IBlockState> blocks = new HashMap<>();

        Template template = world.getStructureTemplateManager().get(world.getMinecraftServer(), id);
        if (template == null)
            return null;

        NBTTagCompound compound = template.writeToNBT(new NBTTagCompound());

        NBTTagList paletteList = compound.getTagList("palette", 10);
        for (int i = 0; i < paletteList.tagCount(); i++) {
            palette.put(i, NBTUtil.readBlockState(paletteList.getCompoundTagAt(i)));
        }

        NBTTagList blocksList = compound.getTagList("blocks", 10);
        for (int j = 0; j < blocksList.tagCount(); ++j) {
            NBTTagCompound nbttagcompound = blocksList.getCompoundTagAt(j);
            NBTTagList nbttaglist2 = nbttagcompound.getTagList("pos", 3);
            BlockPos blockpos = new BlockPos(nbttaglist2.getIntAt(0), nbttaglist2.getIntAt(1), nbttaglist2.getIntAt(2));
            IBlockState iblockstate = palette.get(nbttagcompound.getInteger("state"));

            blocks.put(blockpos, iblockstate);
        }

        structureInfo = new StructureInfo(createBlockPattern(blocks, palette), id, blocks, palette, template.getSize());
        templates.put(id, structureInfo);
        return structureInfo;
    }

    private static BlockPattern createBlockPattern(Map<BlockPos, IBlockState> blocks, BiMap<Integer, IBlockState> palette) {
        int minx, minY, minZ;
        int maxx, maxY, maxZ;

        minx = minY = minZ = Integer.MAX_VALUE;
        maxx = maxY = maxZ = Integer.MIN_VALUE;

        for (BlockPos blockPos : blocks.keySet()) {
            int x = blockPos.getX();
            if (minx > x) {
                minx = x;
            }

            if (maxx < x) {
                maxx = x;
            }

            int y = blockPos.getY();
            if (minY > y) {
                minY = y;
            }

            if (maxY < y) {
                maxY = y;
            }

            int z = blockPos.getZ();
            if (minZ > z) {
                minZ = z;
            }

            if (maxZ < z) {
                maxZ = z;
            }
        }

        Map<Integer, Character> charMap = palette.keySet().stream().collect(Collectors.toMap(x -> x, x -> Character.forDigit(x, 10)));
        BiMap<IBlockState, Integer> inversePallete = palette.inverse();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

        FactoryBlockPattern factoryBlockPattern = FactoryBlockPattern.start();

        for (int y = minY; y <= maxY; y++) {
            List<String> zLines = new ArrayList<>();

            for (int x = minx; x <= maxx; x++) {
                String zLine = "";

                for (int z = minZ; z <= maxZ; z++) {
                    mutableBlockPos.setPos(x, y, z);
                    IBlockState blockState = blocks.get(mutableBlockPos);
                    Integer intId = inversePallete.get(blockState);
                    Character character = charMap.get(intId);
                    zLine += character;
                }

                zLines.add(zLine);
            }

            factoryBlockPattern.aisle(zLines.toArray(new String[0]));
        }

        charMap.forEach((integer, character) -> {
            IBlockState iBlockState = palette.get(integer);

            factoryBlockPattern.where(character, iBlockState.getMaterial() == Material.AIR
                    ? BlockWorldState.hasState(BlockStateMatcher.ANY)
                    : BlockWorldState.hasState(BlockStateMatcher.forBlock(iBlockState.getBlock())));
        });

        return factoryBlockPattern.build();
    }

    public static class StructureInfo {
        final BlockPattern pattern;
        final ResourceLocation id;
        final Map<BlockPos, IBlockState> blocks;
        final BiMap<Integer, IBlockState> palette;
        final BiMap<IBlockState, Integer> paletteInverse;
        private BlockPos size;
        private IBlockState rarestBlock;


        StructureInfo(BlockPattern pattern, ResourceLocation id, Map<BlockPos, IBlockState> blocks, BiMap<Integer, IBlockState> palette, BlockPos size) {
            this.pattern = pattern;
            this.id = id;
            this.blocks = blocks;
            this.palette = palette;
            paletteInverse = palette.inverse();
            this.size = size;

            Map<IBlockState, Integer> countMap = new HashMap<>();

            blocks.forEach((pos, state) -> {
                countMap.compute(state, (state1, integer) -> integer == null ? 1 : integer + 1);
            });

            int count = countMap.values().stream().mapToInt(x -> x).min().orElse(-1);
            rarestBlock = countMap.entrySet().stream().filter(x -> x.getValue() == count).map(Map.Entry::getKey).findFirst().orElse(null);
        }

        public BiMap<Integer, IBlockState> getPalette() {
            return palette;
        }

        public BlockPattern getPattern() {
            return pattern;
        }

        public Map<BlockPos, IBlockState> getBlocks() {
            return blocks;
        }

        public ResourceLocation getId() {
            return id;
        }

        public BlockPattern.PatternHelper match(World world, BlockPos pos) {
            IBlockState state = world.getBlockState(pos);
            if (!paletteInverse.containsKey(state))
                return null;

            Stream<BlockPos.MutableBlockPos> blockPosStream = StreamSupport.stream(BlockPos.getAllInBoxMutable(pos.subtract(size), pos.add(size)).spliterator(), false);
            List<BlockPos> rarestPoses = blockPosStream.filter(x -> world.getBlockState(x) == rarestBlock).map(BlockPos::new).collect(Collectors.toList());
            List<BlockPos> relativePoses = blocks.entrySet().stream().filter(x -> x.getValue() == rarestBlock).map(Map.Entry::getKey).collect(Collectors.toList());

            for (BlockPos rarestPose : rarestPoses) {
                for (BlockPos relativePose : relativePoses) {
                    BlockPattern.PatternHelper match = getPattern().match(world, rarestPose.subtract(relativePose));
                    if (match != null)
                        return match;
                }
            }

            return null;
        }
    }
}
