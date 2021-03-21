package divineadditions.item;

import divineadditions.api.IArmorEssence;
import divineadditions.api.IBlankArmor;
import divineadditions.capability.item_provider.CapabilityItemProvider;
import divineadditions.utils.InventoryHelper;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class ItemBlankArmor extends ItemArmor implements IBlankArmor {
    public ItemBlankArmor(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
        super(materialIn, renderIndexIn, equipmentSlotIn);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        CapabilityItemProvider provider = new CapabilityItemProvider(new ItemStackHandler(32));

        if (nbt != null) {
            provider.deserializeNBT(nbt);
        }

        return provider;
    }

    @Override
    public boolean getShareTag() {
        return super.getShareTag();
    }

    @Nullable
    @Override
    public NBTTagCompound getNBTShareTag(ItemStack stack) {
        return super.getNBTShareTag(stack);
    }

    @Override
    public void readNBTShareTag(ItemStack stack, @Nullable NBTTagCompound nbt) {
        super.readNBTShareTag(stack, nbt.getCompoundTag("tag"));
        InventoryHelper.load((IItemHandlerModifiable) stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), nbt.getCompoundTag("ForgeCaps"));
    }

    @Override
    public boolean isWearing(ItemStack stack, ResourceLocation location) {
        if (stack == null || stack.isEmpty() || location == null)
            return false;

        IItemHandlerModifiable handler = getHandler(stack);
        if (handler == null)
            return false;

        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stackInSlot = handler.getStackInSlot(i);
            if (stackInSlot.getItem() instanceof IArmorEssence
                    && ((IArmorEssence) stackInSlot.getItem()).getDescription(stackInSlot).getRegistryName() == location) {
                return true;
            }
        }

        return false;
    }

    @Override
    public IItemHandlerModifiable getHandler(ItemStack stack) {
        return (IItemHandlerModifiable) stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
    }
}
