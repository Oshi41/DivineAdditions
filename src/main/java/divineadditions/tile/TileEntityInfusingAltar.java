package divineadditions.tile;

import divineadditions.api.IPedestal;
import divineadditions.recipe.InfusingRecipe;
import divineadditions.tile.base.TileSyncBase;
import divineadditions.utils.InventoryHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class TileEntityInfusingAltar extends TileSyncBase implements IPedestal {
    private final List<InfusingRecipe> infusingRecipes;
    private final int radius = 5;

    public TileEntityInfusingAltar() {
        infusingRecipes = StreamSupport.stream(ForgeRegistries.RECIPES.spliterator(), false)
                .filter(x -> x instanceof InfusingRecipe)
                .map(x -> ((InfusingRecipe) x))
                .collect(Collectors.toList());
    }

    public boolean tryInfuse(@Nonnull InfusingRecipe recipe) {
        Map<TileEntity, IItemHandler> pedestals = findPedestals();
        if (pedestals.isEmpty())
            return false;

        IItemHandler catalyst = getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

        pedestals.values().forEach(InventoryHelper::clear);
        pedestals.keySet().forEach(x -> {
            BlockPos pos = x.getPos();
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        });

        ((IItemHandlerModifiable) catalyst).setStackInSlot(0, recipe.getRecipeOutput());
        IBlockState state = world.getBlockState(getPos());
        world.notifyBlockUpdate(getPos(), state, state, 3);

        spawnEffects(world, pedestals.keySet());
        return true;
    }

    public InfusingRecipe findByCatalyst() {
        IItemHandler capability = getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (capability == null)
            return null;

        ItemStack itemStack = capability.getStackInSlot(0);
        return infusingRecipes.stream().filter(x -> x.getCatalyst().apply(itemStack)).findFirst().orElse(null);
    }

    private boolean isMatching(InfusingRecipe recipe, Map<TileEntity, IItemHandler> ingredients, IItemHandler catalyst) {
        if (recipe == null || ingredients == null || ingredients.isEmpty() || catalyst == null)
            return false;

        return recipe.match(catalyst, ingredients.values());
    }

    public boolean isMatching(InfusingRecipe recipe) {
        return recipe.match(getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), findPedestals().values());
    }

    private void spawnEffects(World world, Collection<TileEntity> pedestals) {
        if (world.isRemote) {
            for (int i = 0; i < 10; i++) {
                BlockPos pos = getPos()
                        .add(
                                world.rand.nextInt(4) - 4,
                                world.rand.nextInt(4),
                                world.rand.nextInt(4) - 4

                        );

                world.spawnParticle(
                        EnumParticleTypes.EXPLOSION_LARGE,
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        world.rand.nextFloat() - world.rand.nextFloat(),
                        world.rand.nextFloat() - world.rand.nextFloat(),
                        world.rand.nextFloat() - world.rand.nextFloat()
                );
            }
        } else {
            pedestals.stream().map(x -> new EntityLightningBolt(world, x.getPos().getX(), x.getPos().getY(), x.getPos().getZ(), false))
                    .forEach(world::addWeatherEffect);
        }
    }

    private Map<TileEntity, IItemHandler> findPedestals() {
        return StreamSupport.stream(BlockPos.getAllInBoxMutable(
                getPos().add(-radius, 0, -radius),
                getPos().add(radius, 0, radius)
        ).spliterator(), false)
                .map(x -> world.getTileEntity(x))
                .filter(x -> x != this && x instanceof IPedestal)
                .collect(Collectors.toMap(x -> x, x -> x.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)));
    }

    @Override
    public int getStackSize() {
        return 1;
    }
}
