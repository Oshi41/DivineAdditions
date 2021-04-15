package divineadditions.recipe;

import divineadditions.item.sword.ItemCustomSword;
import divineadditions.item.sword.SwordProperties;
import divineadditions.utils.InventoryHelper;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PotionFurnaceRecipe {
    private final List<ItemStack> inventory;
    private final ItemStack output;
    private final int cookTime;
    private BlockPos cauldron;

    public PotionFurnaceRecipe(List<ItemStack> inventory, ItemStack output, int cookTime, BlockPos cauldron) {
        this.inventory = inventory;
        this.output = output;
        this.cookTime = cookTime;
        this.cauldron = cauldron;
    }

    public static PotionFurnaceRecipe find(IInventory inventory) {
        if (inventory.getSizeInventory() < 3)
            return null;

        for (int i = 0; i < 3; i++) {
            if (inventory.getStackInSlot(i).isEmpty())
                return null;
        }

        if (!(inventory instanceof TileEntity)) {
            return null;
        }

        ItemStack sword = inventory.getStackInSlot(1).copy();
        if (!(sword.getItem() instanceof ItemCustomSword)) {
            return null;
        }

        List<PotionEffect> left = PotionUtils.getEffectsFromStack(inventory.getStackInSlot(0));
        List<PotionEffect> effects = PotionUtils.getEffectsFromStack(inventory.getStackInSlot(2));

        // same size, not empty
        if (effects.size() == 0 || effects.size() != left.size()) {
            return null;
        }

        // the same elements
        if (!left.removeAll(effects) || !left.isEmpty()) {
            return null;
        }

        TileEntity tileEntity = (TileEntity) inventory;
        BlockPos blockPos = tileEntity.getPos();
        World world = tileEntity.getWorld();
        IBlockState blockState = world.getBlockState(blockPos);
        PropertyDirection prop = BlockHorizontal.FACING;

        EnumFacing enumFacing = blockState.getPropertyKeys().contains(prop)
                ? blockState.getValue(prop)
                : EnumFacing.NORTH;

        BlockPos firstCauldron = findFirstCauldron(world, blockPos, enumFacing);
        if (firstCauldron == null)
            return null;

        SwordProperties props = ((ItemCustomSword) sword.getItem()).getSwordProps();
        int cookTime = (props.getAttackEffects(sword).size() + effects.size()) * 200;

        for (PotionEffect effect : effects) {
            props.addAttackEffect(sword, effect);
        }

        return new PotionFurnaceRecipe(InventoryHelper
                .asStream(inventory)
                .limit(3)
                .map(ItemStack::copy)
                .collect(Collectors.toList()), sword, cookTime, firstCauldron);
    }

    @Nullable
    public static BlockPos findFirstCauldron(World world, BlockPos pos, EnumFacing facing) {
        List<BlockPos> poses = Arrays.asList(pos.offset(facing.getOpposite()), pos.offset(facing.rotateYCCW()), pos.offset(facing.rotateYCCW().getOpposite()));
        BlockPos cauldronPos = poses.stream()
                .collect(Collectors.toMap(x -> x, world::getBlockState))
                .entrySet()
                .stream()
                .filter(x -> x.getValue().getBlock() == Blocks.CAULDRON)
                .filter(x -> x.getValue().getValue(BlockCauldron.LEVEL) == 3)
                .map(x -> x.getKey())
                .findFirst()
                .orElse(null);

        return cauldronPos;
    }

    public boolean isMatch(IInventory inventory) {
        if (this.inventory.size() > inventory.getSizeInventory())
            return false;

        for (int i = 0; i < this.inventory.size(); i++) {
            if (!ItemStack.areItemStacksEqual(this.inventory.get(i), inventory.getStackInSlot(i)))
                return false;
        }

        if (!(inventory instanceof TileEntity)) {
            return false;
        }

        TileEntity entity = (TileEntity) inventory;

        return entity.getWorld().getBlockState(getCauldron()).getBlock() == Blocks.CAULDRON;
    }

    public int getCookTime() {
        return cookTime;
    }

    public ItemStack getOutput() {
        return output;
    }

    public BlockPos getCauldron() {
        return cauldron;
    }
}
