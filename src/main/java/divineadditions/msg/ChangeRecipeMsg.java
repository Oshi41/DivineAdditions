package divineadditions.msg;

import divineadditions.recipe.ForgeRecipes;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;

public class ChangeRecipeMsg implements IMessage {
    private ForgeRecipes recipe;

    public ChangeRecipeMsg() {
    }

    public ChangeRecipeMsg(ForgeRecipes recipe) {
        this.recipe = recipe;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        ResourceLocation id = new ResourceLocation(ByteBufUtils.readUTF8String(buf));
        IRecipe iRecipe = ForgeRegistries.RECIPES.getValue(id);

        if (iRecipe instanceof ForgeRecipes) {
            recipe = ((ForgeRecipes) iRecipe);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        String str = "";
        if (recipe != null) {
            str = recipe.getRegistryName().toString();
        }

        ByteBufUtils.writeUTF8String(buf, str);
    }

    @Nullable
    public ForgeRecipes getRecipe() {
        return recipe;
    }
}
