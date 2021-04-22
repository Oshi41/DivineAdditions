package divineadditions.event;

import divineadditions.DivineAdditions;
import divineadditions.api.IInstantRanged;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = DivineAdditions.MOD_ID, value = Side.CLIENT)
public class RenderGameOverlayEventHandler {

    @SubscribeEvent
    public static void handle(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
            renderCrossHairs(event.getResolution(), Minecraft.getMinecraft().player);
        }
    }

    private static void renderCrossHairs(ScaledResolution resolution, EntityPlayer player) {
        if (player == null || Minecraft.getMinecraft().gameSettings.thirdPersonView != 0)
            return;

        ItemStack stack = player.getHeldItemMainhand();
        if (!(stack.getItem() instanceof IInstantRanged))
            return;

        RayTraceResult target = ((IInstantRanged) stack.getItem()).findTarget(player.getEntityWorld(), player);
        if (target.typeOfHit == RayTraceResult.Type.MISS)
            return;

        Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);
        GlStateManager.enableBlend();

        int x = resolution.getScaledWidth();
        int y = resolution.getScaledHeight();

        Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(x / 2 - 4, y / 2 - 5, 3, 7, 9, 1);
        Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(x / 2 - 4, y / 2 + 5, 3, 7, 9, 1);

        Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(x / 2 - 5, y / 2 - 4, 7, 3, 1, 9);
        Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(x / 2 + 5, y / 2 - 4, 7, 3, 1, 9);
    }
}
