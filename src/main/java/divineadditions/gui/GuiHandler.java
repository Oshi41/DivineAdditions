package divineadditions.gui;

import divineadditions.gui.conainter.RifleContainer;
import divineadditions.gui.gui_container.RifleGuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {
    public static final int RifleGuiId = 0;

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case RifleGuiId:
                return new RifleContainer(player);

            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case RifleGuiId:
                return new RifleGuiContainer((Container) getServerGuiElement(ID, player, world, x, y, z), player);

            default:
                return null;
        }
    }
}
