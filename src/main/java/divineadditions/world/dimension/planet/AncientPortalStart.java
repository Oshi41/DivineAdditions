package divineadditions.world.dimension.planet;

import divineadditions.DivineAdditions;
import divineadditions.config.DivineAdditionsConfig;
import divineadditions.config.PlanetConfig;
import divineadditions.world.structure.StructureComponentTemplate;
import divineadditions.world.structure.StructureWorldGen;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

import java.util.List;
import java.util.stream.Collectors;

public class AncientPortalStart extends StructureStart {
    private final List<PlanetConfig> planets;

    public AncientPortalStart(World world, int chunkX, int chunkZ) {
        super(chunkX, chunkZ);

        planets = DivineAdditionsConfig.planetDimensionConfig.possiblePlanets.stream().filter(x -> !x.isAlwaysHalf()).collect(Collectors.toList());
        if (planets.isEmpty()) {
            throw new RuntimeException("No possible planets founded!");
        }

        PlanetConfig config = planets.get(world.rand.nextInt(planets.size()));

        Block top = Blocks.OBSIDIAN;
        Block bottom = Blocks.OBSIDIAN;
        Block in = Blocks.OBSIDIAN;
        Block side = Blocks.OBSIDIAN;
//        Block top = config.getTop().getBlock();
//        Block bottom = config.getBottom().getBlock();
//        Block in = config.getIn().getBlock();
//        Block side = config.getSide().getBlock();

        BlockPos position = new ChunkPos(chunkX, chunkZ).getBlock(0, world.rand.nextInt(200) + 20, 0);
        int radius = 15;

        PlanetWorldGen sphere = new PlanetWorldGen(
                random -> new PlanetConfig(top, bottom, Blocks.AIR, side, 1, position.getY(), position.getY(), radius, radius, false),
                false);

        components.add(new StructureWorldGen(sphere, position));

        PlanetWorldGen platform = new PlanetWorldGen(
                random -> new PlanetConfig(top, bottom, in, side, 1, position.getY(), position.getY(), radius, radius, false),
                true);

        components.add(new StructureWorldGen(platform, position));


        if (world instanceof WorldServer) {
            TemplateManager templateManager = ((WorldServer) world).getStructureTemplateManager();
            ResourceLocation location = new ResourceLocation(DivineAdditions.MOD_ID, "ancient_portal");
            Template template = templateManager.get(world.getMinecraftServer(), location);

            if (template != null) {
                float integrity = 0.8f;
                Rotation rotation = Rotation.values()[world.rand.nextInt(Rotation.values().length)];
                BlockPos size = template.getSize();

                PlacementSettings settings = new PlacementSettings()
                        .setIntegrity(integrity)
                        .setRotation(rotation);

                BlockPos middle = position.add(16 - size.getX() / 2, 0, 16 - size.getZ() / 2);

                components.add(new StructureComponentTemplate(template, middle, settings));
            }
        }

        updateBoundingBox();
    }
}
