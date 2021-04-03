package divineadditions.utils;

import com.google.gson.JsonObject;
import divineadditions.DivineAdditions;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NbtUtils {
    private static final List<Pattern> formatPatterns = Arrays.asList(
            Pattern.compile("[-+]?(?:[0-9]+[.]|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?", 2),
            Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d", 2),
            Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?f", 2),
            Pattern.compile("[-+]?(?:0|[1-9][0-9]*)b", 2),
            Pattern.compile("[-+]?(?:0|[1-9][0-9]*)l", 2),
            Pattern.compile("[-+]?(?:0|[1-9][0-9]*)s", 2)
    );

    private static final Pattern quotedValuePatter = Pattern.compile(":\"([^\"]*)\"");

    public static ItemStack parseStack(JsonObject json, JsonContext context) {
        ItemStack stack = CraftingHelper.getItemStack(json, context);

        if (json.has("nbt")) {
            String rawNbt = json.getAsJsonObject("nbt").toString();

            try {
                NBTTagCompound tag = JsonToNBT.getTagFromJson(clearValues(rawNbt));
                stack.setTagCompound(tag);
            } catch (NBTException e) {
                DivineAdditions.logger.warn(e);
            }
        }

        return stack;
    }

    private static String clearValues(String json) {
        Matcher matcher = quotedValuePatter.matcher(json);

        while (matcher.find()) {
            String group = matcher.group(1);

            if (formatPatterns.stream().anyMatch(x -> x.matcher(group).matches())) {
                json = json.replace("\"" + group + "\"", group);
                matcher = quotedValuePatter.matcher(json);
            }
        }

        return json;
    }

}
