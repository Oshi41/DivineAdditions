package divineadditions.utils;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemStackHelper {
    public static ItemStack shrink(ItemStack stack, EntityLivingBase player, int count) {
        if (player instanceof EntityPlayer && ((EntityPlayer) player).isCreative()) {
            return stack;
        }

        stack.shrink(count);
        return stack;
    }

    public static NBTTagCompound save(ItemStack stack) {
        NBTTagCompound nbt = stack.serializeNBT();
        nbt.setInteger("Count", stack.getCount());
        return nbt;
    }

    public static ItemStack load(NBTTagCompound compound) {
        ItemStack stack = new ItemStack(compound);
        stack.setCount(compound.getInteger("Count"));
        return stack;
    }

    @SideOnly(Side.CLIENT)
    public static List<ITextComponent> printStacks(Map<String, Integer> stacks) {
        ArrayList<ITextComponent> components = new ArrayList<>();

        if (stacks == null || stacks.isEmpty()) {
            return components;
        }

        final IForgeRegistry<Item> registry = ForgeRegistries.ITEMS;

        List<ItemStack> items = stacks.entrySet().stream()
                .collect(Collectors.toMap(x -> new ResourceLocation(x.getKey()), Map.Entry::getValue))
                .entrySet()
                .stream()
                .filter(x -> registry.containsKey(x.getKey()))
                .map(x -> new ItemStack(registry.getValue(x.getKey()), x.getValue()))
                .filter(x -> !x.isEmpty())
                .collect(Collectors.toList());

        for (ItemStack item : items) {
            TextComponentString string = new TextComponentString(item.getDisplayName());

            if (item.getCount() > 1) {
                string.appendText(": " + item.getCount());
            }

            components.add(string);
        }

        return components;
    }

    @SideOnly(Side.CLIENT)
    public static List<ITextComponent> printStacks(String[] stacks) {
        if (stacks == null || stacks.length == 0)
            return new ArrayList<>();

        return printStacks(Arrays.stream(stacks).collect(Collectors.toMap(x -> x, x -> 1)));
    }
}
