package divineadditions.item;

import divineadditions.api.IArmorEssence;
import divineadditions.api.IBlankArmor;
import divineadditions.capability.item_provider.CapabilityItemProvider;
import divineadditions.utils.InventoryHelper;
import divinerpg.enums.ArmorInfo;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ItemBlankArmor extends ItemArmor implements IBlankArmor {
    private final ArmorInfo info;

    public ItemBlankArmor(ArmorMaterial materialIn, EntityEquipmentSlot equipmentSlotIn, ArmorInfo info) {
        super(materialIn, 0, equipmentSlotIn);
        this.info = info;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        IItemHandlerModifiable handler = getHandler(stack);
        if (handler != null) {
            int result = InventoryHelper.asStream(handler)
                    .filter(x -> !x.isEmpty())
                    .mapToInt(ItemStack::getMaxDamage)
                    .sum();

            return result;
        }

        return -1;
    }

    @Override
    public int getDamage(ItemStack stack) {
        IItemHandlerModifiable handler = getHandler(stack);
        if (handler != null) {
            int result = InventoryHelper.asStream(handler)
                    .filter(x -> !x.isEmpty())
                    .mapToInt(ItemStack::getItemDamage)
                    .sum();

            return result;
        }

        return 0;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        IItemHandlerModifiable handler = getHandler(stack);
        if (handler != null) {
            List<ItemStack> essences = InventoryHelper.asStream(handler).filter(x -> !x.isEmpty()).collect(Collectors.toList());
            if (!essences.isEmpty()) {
                ItemStack itemStack = essences.get(itemRand.nextInt(essences.size()));
                int itemDamage = damage - getDamage(stack);
                itemStack.damageItem(itemDamage, null);
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.addAll(info.toString(stack, worldIn));

        IItemHandlerModifiable handler = getHandler(stack);
        if (handler != null) {
            List<ItemStack> essences = InventoryHelper.asStream(handler)
                    .filter(x -> x.getItem() instanceof IArmorEssence)
                    .collect(Collectors.toList());

            if (!essences.isEmpty()) {
                tooltip.add("");
                tooltip.add(new TextComponentTranslation("divineadditions.blank_armor_uning_caps").getFormattedText());
                essences.forEach(x -> x.getItem().addInformation(x, worldIn, tooltip, flagIn));
            }
        }
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        CapabilityItemProvider provider = new CapabilityItemProvider(new ItemStackHandler(32) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return stack.getItem() instanceof IArmorEssence;
            }
        });

        if (nbt != null) {
            provider.deserializeNBT(nbt);
        }

        return provider;
    }

    @Nullable
    @Override
    public NBTTagCompound getNBTShareTag(ItemStack stack) {
        NBTTagCompound sharedTag = super.getNBTShareTag(stack);

        NBTTagCompound tag = new NBTTagCompound();
        if (sharedTag != null)
            tag.setTag("tag", sharedTag);

        IItemHandlerModifiable handler = getHandler(stack);
        if (handler != null) {
            NBTBase nbt = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.getStorage().writeNBT(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, handler, null);
            if (nbt != null)
                tag.setTag("ItemCapability", nbt);
        }

        return tag;
    }

    @Override
    public void readNBTShareTag(ItemStack stack, @Nullable NBTTagCompound nbt) {
        if (nbt == null)
            return;

        super.readNBTShareTag(stack, nbt.getCompoundTag("tag"));

        NBTBase tag = nbt.getTag("ItemCapability");
        IItemHandlerModifiable handler = getHandler(stack);

        if (tag != null && handler != null) {
            CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.getStorage().readNBT(
                    CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
                    handler,
                    null,
                    tag
            );
        }
    }

    @Override
    public boolean isWearing(ItemStack stack, ResourceLocation location) {
        if (stack == null || stack.isEmpty() || location == null)
            return false;

        IItemHandlerModifiable handler = getHandler(stack);
        if (handler == null)
            return false;

        Optional<ItemStack> sameEssence = InventoryHelper.asStream(handler)
                .filter(x -> x.getItem() instanceof IArmorEssence
                        && ((IArmorEssence) x.getItem()).getDescription(x).getRegistryName() == location)
                .findFirst();

        return sameEssence.isPresent();
    }

    @Override
    public IItemHandlerModifiable getHandler(ItemStack stack) {
        return (IItemHandlerModifiable) stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
    }
}
