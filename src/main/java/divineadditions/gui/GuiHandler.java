package divineadditions.gui;

import divineadditions.api.IForgeInventory;
import divineadditions.gui.conainter.ForgeContainer;
import divineadditions.gui.conainter.PotionFurnaceContainer;
import divineadditions.gui.conainter.RifleContainer;
import divineadditions.gui.gui_container.ForgeGuiContainer;
import divineadditions.gui.gui_container.PotionFurnaceGuiContainer;
import divineadditions.gui.gui_container.RifleGuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {
    public static final int RifleGuiId = 0;
    public static final int ForgeGui = 1;
    public static final int PotionFurnace = 2;

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

        switch (ID) {
            case RifleGuiId:
                return new RifleContainer(player);

            case ForgeGui:
                if (tileEntity instanceof IForgeInventory) {
                    return new ForgeContainer(((IForgeInventory) tileEntity), player);
                }

            case PotionFurnace:
                if (tileEntity instanceof IInventory) {
                    return new PotionFurnaceContainer(((IInventory) tileEntity), player);
                }

            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

        switch (ID) {
            case RifleGuiId:
                return new RifleGuiContainer((Container) getServerGuiElement(ID, player, world, x, y, z), player);

            case ForgeGui:
                return new ForgeGuiContainer((ForgeContainer) getServerGuiElement(ID, player, world, x, y, z), player);

            case PotionFurnace:
                if (tileEntity instanceof IInventory) {
                    return new PotionFurnaceGuiContainer((Container) getServerGuiElement(ID, player, world, x, y, z), ((IInventory) tileEntity));
                }

            default:
                return null;
        }
    }
}
