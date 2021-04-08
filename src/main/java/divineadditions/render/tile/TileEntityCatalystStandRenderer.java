package divineadditions.render.tile;

import divineadditions.block.BlockCatalystStand;
import divineadditions.tile.TileEntityCatalystStand;
import divinerpg.utils.Lazy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import openmods.utils.InventoryUtils;

@SideOnly(Side.CLIENT)
public class TileEntityCatalystStandRenderer extends TileEntitySpecialRenderer<TileEntityCatalystStand> {
    private final Lazy<EntityItem> inner = new Lazy<>(() -> new EntityItem(Minecraft.getMinecraft().world));


    @Override
    public void render(TileEntityCatalystStand te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        IItemHandler itemHandler = InventoryUtils.tryGetHandler(te, null);
        if (itemHandler != null) {

            for (int i = 0; i < itemHandler.getSlots(); i++) {
                ItemStack itemStack = itemHandler.getStackInSlot(i);

                if (!itemStack.isEmpty()) {

                    Vec3d position = BlockCatalystStand.getItemPosition(i);

                    double xPos = position.x + x;
                    double yPos = y + (i * 0.2);
                    double zPos = position.z + z;

                    EntityItem entityItem = inner.getValue();
                    entityItem.setItem(itemStack);
                    entityItem.setPosition(xPos, yPos, zPos);

                    if (itemStack.getCount() > 1) {
                        entityItem.setAlwaysRenderNameTag(true);
                        entityItem.setCustomNameTag(itemStack.getCount() + "");
                    }

                    long yaw = getWorld().getTotalWorldTime() % 360;

                    Minecraft.getMinecraft().getRenderManager().renderEntity(entityItem, xPos, yPos, zPos, 0, yaw, false);
                }
            }
        }

        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
    }
}
