package divineadditions.world.dimension.planet;

import divineadditions.config.DivineAdditionsConfig;
import divineadditions.config.PlanetConfig;
import divineadditions.utils.SkippingTemplateProcessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class AncientPortalGen extends WorldGenerator {
    private final List<PlanetConfig> planets;
    private final Template template;

    public AncientPortalGen(Template template) {
        this.template = template;
        planets = DivineAdditionsConfig.planetDimensionConfig.possiblePlanets.stream().filter(x -> !x.isAlwaysHalf()).collect(Collectors.toList());

        if (planets.isEmpty()) {
            throw new RuntimeException("No possible planets founded!");
        }

        if (template == null) {
            throw new RuntimeException("No template for portal!");
        }
    }

    @Override
    public boolean generate(World worldIn, Random rand, final BlockPos original) {
        int radius = 15;

        BlockPos position = new BlockPos(original);

        PlanetConfig config = planets.get(rand.nextInt(planets.size()));

        // center of sphere
        BlockPos middle = original.add(16, 0, 16);

        // checking free space
        if (!PlanetWorldGen.checkIsFree(worldIn, middle, radius)) {
            return false;
        }

        // than checking spawn chance
        if (rand.nextInt(DivineAdditionsConfig.planetDimensionConfig.portalSpawnChance) != 0)
            return false;


        final Block bottom = config.getBottom().getBlock();
        final Block top = config.getTop().getBlock() instanceof BlockFalling ? bottom : config.getTop().getBlock();
        final Block in = config.getIn().getBlock();
        final Block side = config.getSide().getBlock();


        final int currentY = position.getY();

        PlanetWorldGen planetWorldGen = new PlanetWorldGen(
                random -> new PlanetConfig(top, bottom, Blocks.AIR, side, 1, currentY, currentY, radius, radius, false),
                false);

        // placing empty sphere here
        planetWorldGen.generate(worldIn, rand, original);


        planetWorldGen = new PlanetWorldGen(
                random -> new PlanetConfig(top, bottom, in, side, 1, currentY, currentY, radius, radius, true),
                false);

        // fill the sphere with blocks
        planetWorldGen.generate(worldIn, rand, original);

        float integrity = 0.65f;

        BlockPos templatePos = middle.add(-template.getSize().getX() / 2, 1, -template.getSize().getZ() / 2);
        PlacementSettings settings = new PlacementSettings()
                .setIntegrity(integrity);

        template.addBlocksToWorld(worldIn, templatePos, new SkippingTemplateProcessor(templatePos, settings), settings, 2);
        return true;
    }
}
