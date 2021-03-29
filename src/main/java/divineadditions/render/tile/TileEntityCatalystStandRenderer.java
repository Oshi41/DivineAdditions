package divineadditions.render.tile;

import divineadditions.block.BlockCatalystStand;
import divineadditions.render.entity.RenderEntityItemEnhanced;
import divineadditions.tile.TileEntityCatalystStand;
import divinerpg.utils.Lazy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
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
    private final Lazy<RenderEntityItemEnhanced> renderer = new Lazy<>(() -> new RenderEntityItemEnhanced(Minecraft.getMinecraft().getRenderManager(), Minecraft.getMinecraft().getRenderItem(), 0.5f));


    @Override
    public void render(TileEntityCatalystStand te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        IItemHandler itemHandler = InventoryUtils.tryGetHandler(te, null);
        if (itemHandler != null) {

            for (int i = 0; i < itemHandler.getSlots(); i++) {
                ItemStack itemStack = itemHandler.getStackInSlot(i);

                if (!itemStack.isEmpty()) {

                    Vec3d position = BlockCatalystStand.getItemPosition(i);

                    double xPos = x + position.x;
                    double yPos = y + position.y;
                    double zPos = z + position.z;

                    EntityItem entityItem = inner.getValue();
                    entityItem.setItem(itemStack);

                    if (itemStack.getCount() > 1) {
                        entityItem.setAlwaysRenderNameTag(true);
                        entityItem.setCustomNameTag(itemStack.getCount() + "");
                    }

                    GlStateManager.pushMatrix();
                    GlStateManager.disableLighting();
                    long totalWorldTime = getWorld().getTotalWorldTime();

                    entityItem.turn(totalWorldTime, 0);

                    renderer.getValue().doRender(entityItem, xPos, yPos, zPos, 0, (float) Math.sin(totalWorldTime / 360.0) * 360);

                    GlStateManager.enableLighting();
                    GlStateManager.popMatrix();
                }
            }
        }

        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
    }
}
