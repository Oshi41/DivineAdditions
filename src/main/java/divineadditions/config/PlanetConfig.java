package divineadditions.config;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class PlanetConfig {
    private String top = Blocks.STONE.getRegistryName().toString();
    private String bottom = Blocks.STONE.getRegistryName().toString();
    private String in = Blocks.STONE.getRegistryName().toString();
    private String side = Blocks.STONE.getRegistryName().toString();
    private int probability = 10;
    private int yMin = 20;
    private int yMax = 200;
    private int minRadius = 4;
    private int maxRadius = 15;
    private boolean alwaysHalf = false;

    public PlanetConfig() {

    }

    public PlanetConfig(Block top, Block bottom, Block in, Block side, int probability, int yMin, int yMax, int minRadius, int maxRadius) {
        this(top, bottom, in, side, probability, yMin, yMax, minRadius, maxRadius, false);
    }

    public PlanetConfig(Block top, Block bottom, Block in, Block side, int probability, int yMin, int yMax, int minRadius, int maxRadius, boolean alwaysHalf) {
        this.side = side.getRegistryName().toString();
        this.bottom = bottom.getRegistryName().toString();
        this.in = in.getRegistryName().toString();
        this.top = top.getRegistryName().toString();
        this.probability = probability;
        this.yMin = yMin;
        this.yMax = yMax;
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
        this.alwaysHalf = alwaysHalf;
    }

    public IBlockState getTop() {
        return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(top)).getDefaultState();
    }

    public IBlockState getBottom() {
        return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(bottom)).getDefaultState();
    }

    public IBlockState getIn() {
        return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(in)).getDefaultState();
    }

    public IBlockState getSide() {
        return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(side)).getDefaultState();
    }

    public int getProbability() {
        return Math.max(1, probability);
    }

    public int getyMax(World world) {
        return Math.min(yMax, world.getActualHeight());
    }

    public int getyMin() {
        return Math.max(yMin, getMaxRadius() + 1);
    }

    public int getMinRadius() {
        return Math.max(2, Math.min(maxRadius, minRadius));
    }

    public int getMaxRadius() {
        return Math.max(15, Math.max(maxRadius, minRadius));
    }

    public boolean isAlwaysHalf() {
        return alwaysHalf;
    }
}
