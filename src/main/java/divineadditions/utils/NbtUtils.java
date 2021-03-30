package divineadditions.utils;

import com.google.gson.JsonObject;
import divineadditions.DivineAdditions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;

public class NbtUtils {
    public static NBTTagCompound getOrCreateModPlayerPersistTag(Entity player, String modName) {
        if (player == null || modName == null)
            return null;

        NBTTagCompound data = player.getEntityData();
        if (!data.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
            data.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
        }

        NBTTagCompound persistentTag = data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        if (!persistentTag.hasKey(modName)) {
            persistentTag.setTag(modName, new NBTTagCompound());
        }

        return persistentTag.getCompoundTag(modName);
    }

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
