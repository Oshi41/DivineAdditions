package divineadditions.utils;

import com.google.gson.JsonObject;
import divineadditions.DivineAdditions;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;

public class NbtUtils {

    public static ItemStack parseStack(JsonObject json, JsonContext context) {
        ItemStack stack = CraftingHelper.getItemStack(json, context);

        if (json.has("nbt")) {
            String rawNbt = json.getAsJsonObject("nbt").toString();

            try {
                NBTTagCompound tag = JsonToNBT.getTagFromJson(rawNbt);
                stack.setTagCompound(tag);
            } catch (NBTException e) {
                DivineAdditions.logger.warn(e);
            }
        }

        return stack;
    }

}
