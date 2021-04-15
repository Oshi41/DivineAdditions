package divineadditions.gui.gui_container;

import divineadditions.DivineAdditions;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.text.DecimalFormat;

@SideOnly(Side.CLIENT)
public class PotionFurnaceGuiContainer extends GuiContainer {
    private final ResourceLocation background = new ResourceLocation(DivineAdditions.MOD_ID, "textures/gui/potion_furnace.png");
    private final IInventory inventory;
    private final int color = Color.darkGray.getRGB();

    public PotionFurnaceGuiContainer(Container inventorySlotsIn, IInventory inventory) {
        super(inventorySlotsIn);
        this.inventory = inventory;
        this.xSize = 175;
        this.ySize = 165;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(background);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);

        int cookTime = inventory.getField(0);
        int burnTime = inventory.getField(1);
        int totalCookTime = inventory.getField(2);

        if (burnTime > 0) {
            int height = 14;
            int k = getScale(burnTime / 100f, height);
            this.drawTexturedModalRect(i + 149, j + 32 + height - k, 176, height - k, height, k + 1);
        }


        double percentage = (cookTime / (double) totalCookTime) * 100;

        if (percentage > 0)
            drawString(fontRenderer, new DecimalFormat("###").format(percentage) + "%", this.guiLeft + 150, this.guiTop + 5, color);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }

    private int getScale(float percentage, int max) {
        return (int) MathHelper.clamp(percentage * max, 0, max);
    }
}
