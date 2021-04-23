package divineadditions.capability.gravity.source;

import divineadditions.capability.gravity.GravityUtils;
import divineadditions.capability.gravity.source.base.GravitySourceBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ItemGravitySource extends GravitySourceBase<ItemStack> {
    private double multiplier;

    public ItemGravitySource(ItemStack owner, double multiplier) {
        super(owner);
        this.multiplier = multiplier;
    }

    @Override
    public double getMultiplier() {
        return multiplier;
    }

    @Override
    public void setMultiplier(double value) {
        multiplier = value;
    }

    /**
     * Call this on
     * Sould called here: {@link net.minecraft.item.Item#onUpdate(ItemStack, World, Entity, int, boolean)}
     * Pass entity as provider
     *
     * @param provider
     * @return
     */
    @Override
    public boolean applyGravity(ICapabilityProvider provider) {
        if (provider instanceof Entity) {
            Entity entity = (Entity) provider;
            if (canApplyTo(entity)) {
                GravityUtils.applyGravity(entity, this);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canApplyTo(Entity e) {
        ItemStack itemStack = getOwner();
        if (e == null || itemStack == null || itemStack.isEmpty())
            return false;

        IItemHandler handler = e.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (handler == null) {
            return e instanceof EntityItem && ((EntityItem) e).getItem().equals(itemStack);
        }

        for (int i = 0; i < handler.getSlots(); i++) {
            if (handler.getStackInSlot(i).equals(itemStack))
                return true;
        }

        return false;
    }
}
