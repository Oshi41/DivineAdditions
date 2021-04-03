package divineadditions.gui.gui_container;

import divineadditions.DivineAdditions;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

public class RifleGuiContainer extends GuiContainer {
    private final InventoryPlayer inventoryPlayer;
    private final ResourceLocation background = new ResourceLocation(DivineAdditions.MOD_ID, "textures/gui/rifle_gui.png");

    public RifleGuiContainer(Container inventorySlotsIn, EntityPlayer player) {
        super(inventorySlotsIn);
        inventoryPlayer = player.inventory;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(background);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);

//        int inventoryRows = 4;
//
//        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//        this.mc.getTextureManager().bindTexture(background);
//        int i = (this.width - this.xSize) / 2;
//        int j = (this.height - this.ySize) / 2;
//        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, inventoryRows * 18 + 17);
//        this.drawTexturedModalRect(i, j + inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
    }
}
