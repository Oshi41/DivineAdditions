package divineadditions.tile;

import divineadditions.api.IPhantomRender;
import divineadditions.config.DivineAdditionsConfig;
import divineadditions.holders.Blocks;
import divineadditions.item.sword.ItemCustomSword;
import divineadditions.recipe.PotionFurnaceRecipe;
import divineadditions.tile.base.TileEntitySync;
import divineadditions.utils.InventoryHelper;
import divineadditions.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TileEntityPotionFurnace extends TileEntitySync implements ITickable, ISidedInventory, IPhantomRender {
    private static final IBlockState filledCauldron = net.minecraft.init.Blocks.CAULDRON.getDefaultState().withProperty(BlockCauldron.LEVEL, 3);
    private static final int[] SLOTS_TOP = IntStream.range(0, 4).toArray();
    private static final int[] SLOTS_SIDES_FUEL = new int[]{3};

    private final InventoryBasic inner;

    private int cookTime;
    private int burnTime;
    private int totalCookTime;
    private BlockPos cauldron = BlockPos.ORIGIN;
    private PotionFurnaceRecipe currentRecipe;

    public TileEntityPotionFurnace() {
        inner = new InventoryBasic("name", false, 4);
    }

    @Override
    public void markDirty() {
        inner.markDirty();
        super.markDirty();

        PotionFurnaceRecipe current = getCurrentRecipe();

        if (current == null) {
            setCurrentRecipe(PotionFurnaceRecipe.find(this));
        } else {
            if (!current.isMatch(this)) {
                setCurrentRecipe(null);
            }
        }
    }

    @Override
    public void update() {
        boolean wasCooking = isCooking();

        if (isBurning()) {
            burnTime = MathHelper.clamp(burnTime - 1, 0, Integer.MAX_VALUE);
        }

        PotionFurnaceRecipe currentRecipe = getCurrentRecipe();

        if (currentRecipe == null) {
            cookTime = 0;
            recheckState(wasCooking, false);
            return;
        }

        if (world.isRemote) {
            if (cauldron != BlockPos.ORIGIN && isCooking()) {
                world.spawnParticle(EnumParticleTypes.SPELL,
                        cauldron.getX() + 0.5,
                        cauldron.getY() + 1,
                        cauldron.getZ() + 0.5,
                        world.rand.nextFloat() - world.rand.nextFloat(),
                        world.rand.nextFloat() + world.rand.nextFloat(),
                        world.rand.nextFloat() - world.rand.nextFloat()
                );
            }
        }

        // Trying to perform burn fuel (on server)
        if (!isBurning()) {
            for (int index : SLOTS_SIDES_FUEL) {
                ItemStack fuel = getStackInSlot(index);
                int burnTime = getBurnTime(fuel);
                if (burnTime > 0) {
                    decrStackSize(index, 1);
                    this.burnTime = burnTime;
                    markDirty();
                    break;
                }
            }
        }

        // not burning, remove cooking progress
        if (!isBurning()) {
            cookTime = MathHelper.clamp(cookTime - 2, 0, Integer.MAX_VALUE);
            recheckState(wasCooking, false);
            return;
        }

        cookTime++;
        recheckState(wasCooking, false);

        if (totalCookTime <= cookTime) {
            Vec3d position = new Vec3d(cauldron.getX() + 0.5, cauldron.getY() + 1.5, cauldron.getZ());

            if (world.isRemote) {
                for (int i = 0; i < 5; i++) {
                    world.spawnParticle(
                            EnumParticleTypes.EXPLOSION_LARGE,
                            position.x,
                            position.y,
                            position.z,
                            (world.rand.nextFloat() - world.rand.nextFloat()) * 0.1,
                            world.rand.nextFloat() * 0.1,
                            (world.rand.nextFloat() - world.rand.nextFloat()) * 0.1);
                }

            } else {
                ItemStack result = getCurrentRecipe().getOutput();
                BlockPos cauldron = getCurrentRecipe().getCauldron();
                IBlockState blockState = world.getBlockState(cauldron);

                if (blockState.getBlock() == net.minecraft.init.Blocks.CAULDRON) {
                    ((BlockCauldron) blockState.getBlock()).setWaterLevel(world, cauldron, blockState, 0);
                    EntityItem item = new EntityItem(world, position.x, position.y, position.z, result);
                    item.motionZ = item.motionX = 0;
                    item.setDefaultPickupDelay();
                    world.spawnEntity(item);
                }

                for (int i = 0; i < 3; i++) {
                    decrStackSize(i, 1);
                }

                setCurrentRecipe(null);
                recheckState(wasCooking, true);
            }
        }
    }

    @Override
    public String getName() {
        return inner.getName();
    }

    @Override
    public boolean hasCustomName() {
        return inner.hasCustomName();
    }

    @Override
    public int getSizeInventory() {
        return inner.getSizeInventory();
    }

    @Override
    public boolean isEmpty() {
        return inner.isEmpty();
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return inner.getStackInSlot(i);
    }

    @Override
    public ItemStack decrStackSize(int i, int i1) {
        ItemStack stack = inner.decrStackSize(i, i1);
        if (!stack.isEmpty()) {
            this.markDirty();
        }
        return stack;
    }

    @Override
    public ItemStack removeStackFromSlot(int i) {
        ItemStack result = inner.removeStackFromSlot(i);
        markDirty();
        return result;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemStack) {
        inner.setInventorySlotContents(i, itemStack);
        markDirty();
    }

    @Override
    public int getInventoryStackLimit() {
        return inner.getInventoryStackLimit();
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer entityPlayer) {
        return inner.isUsableByPlayer(entityPlayer) && world.getTileEntity(getPos()) == this;
    }

    @Override
    public void openInventory(EntityPlayer entityPlayer) {
        inner.openInventory(entityPlayer);
    }

    @Override
    public void closeInventory(EntityPlayer entityPlayer) {
        inner.closeInventory(entityPlayer);
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemStack) {
        if (!inner.isItemValidForSlot(i, itemStack)) {
            return false;
        }

        switch (i) {
            case 0:
            case 2:
                return PotionUtils.getEffectsFromStack(itemStack).size() > 0;

            case 1:
                return itemStack.getItem() instanceof ItemCustomSword;

            // fuel slot
            default:
                return getBurnTime(itemStack) > 0;
        }
    }

    @Override
    public int getField(int i) {
        switch (i) {
            case 0:
                return cookTime;

            case 1:
                return burnTime;

            case 2:
                return totalCookTime;

            default:
                return 0;
        }
    }

    @Override
    public void setField(int i, int value) {
        switch (i) {
            case 0:
                cookTime = value;
                break;

            case 1:
                burnTime = value;
                break;

            case 2:
                totalCookTime = value;
                break;
        }
    }

    @Override
    public int getFieldCount() {
        return 3;
    }

    @Override
    public void clear() {
        inner.clear();
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        if (side == EnumFacing.DOWN) {
            return SLOTS_SIDES_FUEL;
        } else {
            return SLOTS_TOP;
        }
    }

    @Override
    public boolean canInsertItem(int i, ItemStack itemStack, EnumFacing enumFacing) {
        return this.isItemValidForSlot(i, itemStack);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        if (direction == EnumFacing.DOWN) {
            if (Arrays.stream(SLOTS_SIDES_FUEL).anyMatch(x -> x == index)) {
                Item item = stack.getItem();
                return item == Items.WATER_BUCKET || item == Items.BUCKET;
            }
        }

        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        cookTime = compound.getInteger("cookTime");
        burnTime = compound.getInteger("burnTime");
        totalCookTime = compound.getInteger("totalCookTime");
        cauldron = BlockPos.fromLong(compound.getLong("cauldron"));
        InventoryHelper.load(inner, compound.getCompoundTag("Inv"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound nbt = super.writeToNBT(compound);

        compound.setInteger("cookTime", cookTime);
        compound.setInteger("burnTime", burnTime);
        compound.setInteger("totalCookTime", totalCookTime);
        compound.setLong("cauldron", cauldron.toLong());
        compound.setTag("Inv", InventoryHelper.save(inner));

        return nbt;
    }

    private boolean isBurning() {
        return burnTime > 0;
    }

    private boolean isCooking() {
        return cookTime > 0;
    }

    private void recheckState(boolean wasCooking, boolean forceMarkDirty) {
        if (wasCooking != isCooking()) {
            forceMarkDirty = true;

            Block toPlace = isCooking() ? Blocks.potion_furnace_on : Blocks.potion_furnace;
            WorldUtils.swapBlocks(world, getPos(), state -> toPlace.getDefaultState().withProperty(BlockHorizontal.FACING, state.getValue(BlockHorizontal.FACING)));
        }

        if (forceMarkDirty) {
            markDirty();
        }
    }

    private int getBurnTime(ItemStack stack) {
        Integer burnTime = DivineAdditionsConfig.potionFurnaceConfig.potionFurnaceFuel.get(stack.getItem().getRegistryName().toString());

        if (burnTime == null) {
            burnTime = -1;
        }

        return burnTime;
    }

    public PotionFurnaceRecipe getCurrentRecipe() {
        return currentRecipe;
    }

    public void setCurrentRecipe(PotionFurnaceRecipe currentRecipe) {
        if (this.currentRecipe == currentRecipe)
            return;

        this.currentRecipe = currentRecipe;

        cauldron = currentRecipe != null ? currentRecipe.getCauldron() : BlockPos.ORIGIN;
        totalCookTime = currentRecipe != null ? currentRecipe.getCookTime() : -1;
        cookTime = currentRecipe == null ? 0 : cookTime;
    }

    @Override
    public Map<BlockPos, IBlockState> getPhantomBlocks() {
        if (getCurrentRecipe() != null || world == null)
            return null;

        IBlockState state = world.getBlockState(getPos());
        if (!state.getPropertyKeys().contains(BlockHorizontal.FACING))
            return null;

        EnumFacing facing = state.getValue(BlockHorizontal.FACING);

        if (PotionFurnaceRecipe.findFirstCauldron(world, getPos(), facing) != null)
            return null;

        return Stream.of(
                getPos().offset(facing.getOpposite()),
                getPos().offset(facing.rotateYCCW()),
                getPos().offset(facing.rotateYCCW().getOpposite())
        ).filter(x -> world.isAirBlock(x))
                .collect(Collectors.toMap(x -> x, x -> filledCauldron));
    }
}
