package divineadditions.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ImmortalItem extends Item {
    public static Entity createImmortalEntity(World world, Entity location, ItemStack itemstack) {
        if (location instanceof EntityItem) {
            EntityItem entityItem = (EntityItem) location;

            entityItem.setNoDespawn();
            entityItem.setDefaultPickupDelay();
            entityItem.setEntityInvulnerable(true);
        }

        return null;
    }

    @Nullable
    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        return createImmortalEntity(world, location, itemstack);
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }
}
