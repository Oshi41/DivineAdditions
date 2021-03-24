package divineadditions.config;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class PlanetConfig {
    public String top = Blocks.STONE.getRegistryName().toString();
    public String bottom = Blocks.STONE.getRegistryName().toString();
    public String in = Blocks.STONE.getRegistryName().toString();
    public String out = Blocks.STONE.getRegistryName().toString();
    public int probability;

    public PlanetConfig() {

    }

    public PlanetConfig(Block top, Block bottom, Block in, Block out, int probability) {
        this.out = out.getRegistryName().toString();
        this.bottom = bottom.getRegistryName().toString();
        this.in = in.getRegistryName().toString();
        this.top = top.getRegistryName().toString();
        this.probability = probability;
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

    public IBlockState getOut() {
        return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(out)).getDefaultState();
    }
}
