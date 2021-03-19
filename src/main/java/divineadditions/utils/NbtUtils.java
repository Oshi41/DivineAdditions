package divineadditions.utils;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

public class NbtUtils {
    public final static String persistentTagName = "PlayerPersisted";

    public static NBTTagCompound getOrCreateModPlayerPersistTag(Entity player, String modName) {
        if (player == null || modName == null)
            return null;

        NBTTagCompound data = player.getEntityData();
        if (!data.hasKey(persistentTagName)) {
            data.setTag(persistentTagName, new NBTTagCompound());
        }

        NBTTagCompound persistentTag = data.getCompoundTag(persistentTagName);
        if (!persistentTag.hasKey(modName)) {
            persistentTag.setTag(modName, new NBTTagCompound());
        }

        return persistentTag.getCompoundTag(modName);
    }
}
