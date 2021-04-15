package divineadditions.tile;

import divineadditions.config.DivineAdditionsConfig;
import divineadditions.holders.Blocks;
import divineadditions.item.sword.ItemCustomSword;
import divineadditions.recipe.PotionFurnaceRecipe;
import divineadditions.utils.InventoryHelper;
import divineadditions.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.stream.IntStream;

public class TileEntityPotionFurnace extends TileEntity implements ITickable, ISidedInventory {
    private static final int[] SLOTS_TOP = IntStream.range(0, 3).toArray();
    private static final int[] SLOTS_SIDES_FUEL = new int[]{3};

    private final IInventory inner;

    private int cookTime;
    private int burnTime;
    private int totalCookTime;
    private PotionFurnaceRecipe currentRecipe;

    public TileEntityPotionFurnace() {
        inner = new InventoryBasic("name", false, 4);
    }

    @Override
    public void update() {
        boolean hadRecipe = currentRecipe != null;
        boolean wasBurning = isBurning();

        if (isBurning()) {
            burnTime = MathHelper.clamp(burnTime - 1, 0, Integer.MAX_VALUE);
        }


        if (world.isRemote) {
            recheckState(wasBurning, false);

            if (currentRecipe != null && isBurning()) {
                BlockPos cauldron = currentRecipe.getCauldron();

                world.spawnParticle(EnumParticleTypes.SPELL,
                        cauldron.getX() + 0.5,
                        cauldron.getY() + 1,
                        cauldron.getZ() + 0.5,
                        world.rand.nextFloat() - world.rand.nextFloat(),
                        world.rand.nextFloat() + world.rand.nextFloat(),
                        world.rand.nextFloat() - world.rand.nextFloat()
                );
            }

            return;
        }

        if (currentRecipe != null && !currentRecipe.isMatch(this)) {
            currentRecipe = null;
        }

        if (currentRecipe == null) {
            currentRecipe = PotionFurnaceRecipe.find(this);
        }

        // recipe was not crafted
        if (currentRecipe == null) {
            cookTime = 0;
            recheckState(wasBurning, false);
            return;
        }

        totalCookTime = currentRecipe.getCookTime();

        // begin crafting recipe
        if (!isBurning()) {
            for (int index : SLOTS_SIDES_FUEL) {
                ItemStack fuel = getStackInSlot(index);
                int burnTime = getBurnTime(fuel);
                if (burnTime > 0) {
                    decrStackSize(index, 1);
                    this.burnTime = burnTime;
                    break;
                }
            }
        }

        if (isBurning()) {
            cookTime++;


            if (currentRecipe.getCookTime() <= cookTime) {

                ItemStack result = currentRecipe.getOutput();
                BlockPos cauldron = currentRecipe.getCauldron();
                IBlockState blockState = world.getBlockState(cauldron);
                if (blockState.getBlock() == net.minecraft.init.Blocks.CAULDRON) {
                    ((BlockCauldron) blockState.getBlock()).setWaterLevel(world, cauldron, blockState, 0);
                    EntityItem item = new EntityItem(world, cauldron.getX() + 0.5, cauldron.getY() + 1.5, cauldron.getZ(), result);
                    item.motionY = world.rand.nextFloat();
                    item.setDefaultPickupDelay();
                    world.spawnEntity(item);
                }

                for (int i = 0; i < 3; i++) {
                    decrStackSize(i, 1);
                }

                for (int i = 0; i < 5; i++) {
                    world.spawnParticle(EnumParticleTypes.SPELL,
                            cauldron.getX(),
                            cauldron.getY() + 1,
                            cauldron.getZ(),
                            world.rand.nextFloat() - world.rand.nextFloat(),
                            world.rand.nextFloat(),
                            world.rand.nextFloat() - world.rand.nextFloat()
                    );
                }

                currentRecipe = null;
            }
        }

        recheckState(wasBurning, (currentRecipe != null) != hadRecipe);
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
    public ITextComponent getDisplayName() {
        return inner.getDisplayName();
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
        return inner.decrStackSize(i, i1);
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
    public void markDirty() {
        inner.markDirty();
        super.markDirty();
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer entityPlayer) {
        return inner.isUsableByPlayer(entityPlayer);
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
        }

        return 0;
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
            return side == EnumFacing.UP ? SLOTS_TOP : SLOTS_SIDES_FUEL;
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
        InventoryHelper.load(inner, compound.getCompoundTag("Inv"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound nbt = super.writeToNBT(compound);

        compound.setInteger("cookTime", cookTime);
        compound.setInteger("burnTime", burnTime);
        compound.setInteger("totalCookTime", totalCookTime);
        compound.setTag("Inv", InventoryHelper.save(inner));

        return nbt;
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, getBlockMetadata(), getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        readFromNBT(tag);
    }

    private boolean isBurning() {
        return burnTime > 0;
    }

    private void recheckState(boolean wasBurning, boolean forceMarkDirty) {
        if (wasBurning != isBurning()) {
            forceMarkDirty = true;

            Block toPlace = isBurning() ? Blocks.potion_furnace_on : Blocks.potion_furnace;
            WorldUtils.swapBlocks(world, getPos(), state -> toPlace.getDefaultState().withProperty(BlockHorizontal.FACING, state.getValue(BlockHorizontal.FACING)));
        }

        if (forceMarkDirty) {
            markDirty();
        }
    }

    private int getBurnTime(ItemStack stack) {
        Integer burnTime = DivineAdditionsConfig.potionFurnaceFuel.get(stack.getItem().getRegistryName().toString());

        if (burnTime == null) {
            burnTime = -1;
        }

        return burnTime;
    }
}
