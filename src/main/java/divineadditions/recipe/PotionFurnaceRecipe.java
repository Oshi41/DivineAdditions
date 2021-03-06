package divineadditions.recipe;

import divineadditions.config.DivineAdditionsConfig;
import divineadditions.holders.Items;
import divineadditions.item.sword.ItemCustomSword;
import divineadditions.item.sword.SwordProperties;
import divineadditions.utils.InventoryHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.BlockHorizontal;
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

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PotionFurnaceRecipe {
    private static final int baseCookTime = 300;

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

        TileEntity tileEntity = (TileEntity) inventory;
        BlockPos blockPos = tileEntity.getPos();
        World world = tileEntity.getWorld();

        ItemStack sword = inventory.getStackInSlot(1).copy();
        if (!(sword.getItem() instanceof ItemCustomSword)) {
            return null;
        }

        ItemStack firstPotion = inventory.getStackInSlot(0);
        ItemStack secondPotion = inventory.getStackInSlot(2);

        if (firstPotion.getItem() != secondPotion.getItem()
                || firstPotion.getItem() != Items.potion_bucket) {
            return null;
        }

        List<PotionEffect> left = PotionUtils.getEffectsFromStack(firstPotion);
        List<PotionEffect> effects = PotionUtils.getEffectsFromStack(secondPotion);

        // same size, not empty
        if (effects.size() == 0 || effects.size() != left.size()) {
            return null;
        }

        // the same elements
        if (!left.removeAll(effects) || !left.isEmpty()) {
            return null;
        }

        BlockPos firstCauldron = findFirstCauldron(world, blockPos, true);
        if (firstCauldron == null)
            return null;

        SwordProperties props = ((ItemCustomSword) sword.getItem()).getSwordProps();

        getSword(tileEntity, sword, props, effects);

        int totalEffectsCount = Math.max(props.getAttackEffects(sword).size(), props.getDefendEffects(sword).size());

        // More than machine can accept
        if (totalEffectsCount > DivineAdditionsConfig.potionFurnaceConfig.maxPotionsCount) {
            return null;
        }

        return new PotionFurnaceRecipe(InventoryHelper
                .asStream(inventory)
                .limit(3)
                .map(ItemStack::copy)
                .collect(Collectors.toList()), sword, totalEffectsCount * baseCookTime, firstCauldron);
    }

    private static void getSword(TileEntity entity, ItemStack sword, SwordProperties properties, List<PotionEffect> effects) {
        World world = entity.getWorld();
        IBlockState blockState = world.getBlockState(entity.getPos());
        Block block = blockState.getBlock();

        if (block == divineadditions.holders.Blocks.attack_potion_furnace || block == divineadditions.holders.Blocks.attack_potion_furnace_on) {
            for (PotionEffect effect : effects) {
                properties.addAttackEffect(sword, effect);
            }
        }

        if (block == divineadditions.holders.Blocks.defence_potion_furnace || block == divineadditions.holders.Blocks.defence_potion_furnace_on) {
            for (PotionEffect effect : effects) {
                properties.addDefendEffect(sword, effect);
            }
        }
    }

    @Nonnull
    public static BlockPos findFirstCauldron(World world, BlockPos pos, boolean checkLevel) {
        IBlockState blockState = world.getBlockState(pos);

        if (!blockState.getPropertyKeys().contains(BlockHorizontal.FACING))
            return BlockPos.ORIGIN;

        EnumFacing facing = blockState.getValue(BlockHorizontal.FACING);

        List<BlockPos> poses = Arrays.asList(pos.offset(facing.getOpposite()), pos.offset(facing.rotateYCCW()), pos.offset(facing.rotateYCCW().getOpposite()));
        BlockPos cauldronPos = poses.stream()
                .collect(Collectors.toMap(x -> x, world::getBlockState))
                .entrySet()
                .stream()
                .filter(x -> x.getValue().getBlock() == Blocks.CAULDRON)
                .filter(x -> !checkLevel || x.getValue().getValue(BlockCauldron.LEVEL) == 3)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(BlockPos.ORIGIN);

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
