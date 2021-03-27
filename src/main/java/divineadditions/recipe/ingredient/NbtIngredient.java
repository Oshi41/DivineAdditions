package divineadditions.recipe.ingredient;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.crafting.IngredientNBT;

import javax.annotation.Nullable;

public class NbtIngredient extends IngredientNBT {
    private ItemStack stack;
    private boolean checkSize;

    public NbtIngredient(ItemStack stack, boolean checkSize) {
        super(stack);
        this.stack = stack;
        this.checkSize = checkSize;
    }

    @Override
    public boolean apply(@Nullable ItemStack input) {
        if (input == null || !ItemStack.areItemsEqual(input, stack)) {
            return false;
        }

        if (checkSize && input.getCount() < stack.getCount()) {
            return false;
        }

        return smoothCompare(((NBTBase) stack.getTagCompound()), input.getTagCompound());
    }

    private boolean smoothCompare(NBTBase left, NBTBase right) {
        if (left == null)
            return true;

        if (right == null)
            return false;

        if (left instanceof NBTTagCompound) {
            return right instanceof NBTTagCompound && smoothCompare(((NBTTagCompound) left), ((NBTTagCompound) right));
        } else if (left instanceof NBTTagList) {
            return right instanceof NBTTagList && smoothCompare(((NBTTagList) left), ((NBTTagList) right));
        } else {
            return left.equals(right);
        }


    }

    private boolean smoothCompare(NBTTagCompound left, NBTTagCompound right) {
        for (String key : left.getKeySet()) {
            NBTBase x = left.getTag(key);
            NBTBase y = right.getTag(key);

            if (!smoothCompare(x, y)) {
                return false;
            }
        }

        return true;
    }

    private boolean smoothCompare(NBTTagList left, NBTTagList right) {
        for (int i = 0; i < left.tagCount(); i++) {
            if (!smoothCompare(left.get(i), right.get(i))) {
                return false;
            }
        }

        return true;
    }
}
