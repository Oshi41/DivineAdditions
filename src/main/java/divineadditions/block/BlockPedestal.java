package divineadditions.block;

import divineadditions.tile.TileEntityStackHolder;
import divineadditions.utils.InventoryHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import openmods.utils.InventoryUtils;

import javax.annotation.Nullable;

public class BlockPedestal extends BlockContainer {

    public BlockPedestal() {
        super(Material.ROCK, MapColor.WHITE_STAINED_HARDENED_CLAY);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityStackHolder();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IItemHandler itemHandler = InventoryUtils.tryGetHandler(worldIn, pos, facing);
        if (itemHandler != null) {
            ItemStack heldItem = playerIn.getHeldItem(hand);
            ItemStack pedestalItem = itemHandler.getStackInSlot(0);

            if (pedestalItem.isEmpty()) {
                if (itemHandler.isItemValid(0, heldItem)) {
                    itemHandler.insertItem(0, heldItem.copy(), false);
                    heldItem.shrink(itemHandler.getStackInSlot(0).getCount());
                    return true;
                }
            } else {
                onBlockClicked(worldIn, pos, playerIn);
                return true;
            }
        }

        return false;
    }

    @Override
    public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
        IItemHandler itemHandler = InventoryUtils.tryGetHandler(worldIn, pos, null);
        if (itemHandler != null) {
            ItemStack itemStack = itemHandler.getStackInSlot(0);
            if (!itemStack.isEmpty()) {

                ItemStack copy = itemStack.copy();

                if (!playerIn.inventory.addItemStackToInventory(copy)) {
                    Block.spawnAsEntity(worldIn, pos, copy);
                }

                itemStack.shrink(itemStack.getCount());
            }
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        IItemHandler handler = InventoryUtils.tryGetHandler(worldIn, pos, null);

        if (handler != null) {
            InventoryHelper.dropAll(handler, worldIn, pos);
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
}
