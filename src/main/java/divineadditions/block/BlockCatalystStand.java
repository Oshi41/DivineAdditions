package divineadditions.block;

import divineadditions.tile.TileEntityCatalystStand;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import openmods.utils.InventoryUtils;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockCatalystStand extends BlockContainer {
    protected static final AxisAlignedBB BASE_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D);
    protected static final AxisAlignedBB STICK_AABB = new AxisAlignedBB(0.4375D, 0.0D, 0.4375D, 0.5625D, 0.875D, 0.5625D);

    /**
     * Made it a bit higher, so {@link AxisAlignedBB#contains(Vec3d)} should see the interaction
     */
    protected static final Map<Integer, AxisAlignedBB> plates = new HashMap<Integer, AxisAlignedBB>() {{
        put(0, new AxisAlignedBB(9 / 16., 0.0D, 4 / 16.,
                16 / 16., 3 / 16., 12 / 16.));

        put(1, new AxisAlignedBB(0 / 16., 0.0D, 0 / 16.,
                8 / 16., 3 / 16., 8 / 16.));

        put(2, new AxisAlignedBB(0 / 16., 0.0D, 9 / 16.,
                8 / 16., 3 / 16., 16 / 16.));
    }};

    public BlockCatalystStand() {
        super(Material.IRON, MapColor.PURPLE);
    }

    @SideOnly(Side.CLIENT)
    public static Vec3d getItemPosition(int slot) {
        AxisAlignedBB alignedBB = plates.get(slot);
        if (alignedBB != null) {
            Vec3d center = alignedBB.getCenter().addVector(0, alignedBB.maxY, 0);
            return center;
        }

        return Vec3d.ZERO;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityCatalystStand();
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, STICK_AABB);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, BASE_AABB);
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BASE_AABB;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        Vec3d hitVec = new Vec3d(hitX, hitY, hitZ);
        if (interact(worldIn, pos, state, playerIn, hand, hitVec, playerIn.isSneaking())) {
            return true;
        }

        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
        Vec3d positionEyes = playerIn.getPositionEyes(1);

        RayTraceResult result = worldIn.rayTraceBlocks(positionEyes, positionEyes.add(playerIn.getLookVec().scale(5)));
        if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
            Vec3d vec = result.hitVec.subtract(pos.getX(), pos.getY(), pos.getZ());
            interact(worldIn, pos, worldIn.getBlockState(pos), playerIn, EnumHand.MAIN_HAND, vec, true);
        }
    }

    protected boolean interact(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, Vec3d hitVec, boolean pulling) {
        Integer integer = plates.entrySet().stream().filter(x -> x.getValue().contains(hitVec)).map(x -> x.getKey()).findFirst().orElse(null);
        if (integer == null)
            return false;

        IItemHandler itemHandler = InventoryUtils.tryGetHandler(worldIn, pos, null);
        if (itemHandler == null)
            return false;

        ItemStack heldItem = playerIn.getHeldItem(hand);

        if (!pulling && !heldItem.isEmpty()) {
            ItemStack result = itemHandler.insertItem(integer, heldItem, false);
            if (!ItemStack.areItemStacksEqual(result, heldItem)) {
                playerIn.setHeldItem(hand, result);
                return true;
            }
        }

        ItemStack stackInSlot = itemHandler.getStackInSlot(integer);
        if (stackInSlot.isEmpty()) {
            return false;
        }

        if (!playerIn.isSneaking()) {
            int stackSize = stackInSlot.getMaxStackSize();
            if (stackSize < stackInSlot.getCount()) {
                stackInSlot = stackInSlot.copy();
                stackInSlot.setCount(stackSize);
            }
        }

        int prevSize = stackInSlot.getCount();

        if (!playerIn.inventory.addItemStackToInventory(stackInSlot)) {
            Block.spawnAsEntity(worldIn, pos, stackInSlot);
        }

        itemHandler.getStackInSlot(integer).shrink(prevSize);

        return true;
    }
}
