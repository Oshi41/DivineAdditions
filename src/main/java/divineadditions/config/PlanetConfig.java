package divineadditions.config;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class PlanetConfig {
    @Config.Comment("Top block for planetoid")
    public String top = Blocks.STONE.getRegistryName().toString();
    @Config.Comment("Bottom block for planetoid")
    public String bottom = Blocks.STONE.getRegistryName().toString();
    @Config.Comment("Block inside the planetoid")
    public String in = Blocks.STONE.getRegistryName().toString();
    @Config.Comment("Block placed on sides")
    public String out = Blocks.STONE.getRegistryName().toString();
    @Config.Comment({"Probability for island, Counting as 1/x.",
            "100 - means island will generate 10 times rare than with 10"})
    @Config.RangeInt(min = 1)
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
