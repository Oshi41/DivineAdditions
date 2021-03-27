package divineadditions.block;

import divineadditions.recipe.InfusingRecipe;
import divineadditions.tile.TileEntityInfusingAltar;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockInfusingAltar extends BlockPedestal {
    protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D);

    public BlockInfusingAltar() {
        super(Material.ROCK, MapColor.GRAY);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof TileEntityInfusingAltar) {
            TileEntityInfusingAltar infusingAltar = (TileEntityInfusingAltar) tileEntity;

            InfusingRecipe recipe = infusingAltar.findByCatalyst();
            if (recipe != null) {
                if (infusingAltar.isMatching(recipe)) {
                    playerIn.sendMessage(new TextComponentTranslation("divineadditions.infusing_altar." + recipe.type));
                } else {
                    playerIn.sendMessage(new TextComponentTranslation("divineadditions.infusing_altar.missing_items"));
                }

                return true;
            }
        }


        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityInfusingAltar();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return AABB;
    }
}
