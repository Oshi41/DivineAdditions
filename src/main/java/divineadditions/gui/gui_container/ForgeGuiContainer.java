package divineadditions.gui.gui_container;

import com.sun.javafx.geom.Rectangle;
import divineadditions.DivineAdditions;
import divineadditions.api.IForgeInventory;
import divineadditions.gui.conainter.ForgeContainer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fluids.IFluidTank;

import java.text.DecimalFormat;

public class ForgeGuiContainer extends GuiContainer {
    private static final Rectangle dnaRectangle = new Rectangle(4, 9, 18, 68);
    private static final Rectangle cageMobRect = new Rectangle(4, 83, 18, 18);
    private static final Rectangle dnaFillingRectOut = new Rectangle(190, 16, 12, 53);
    private static final Rectangle dnaFillingRect = new Rectangle(7, 16, 12, 53);
    private final ResourceLocation background = new ResourceLocation(DivineAdditions.MOD_ID, "textures/gui/forge.png");
    private IForgeInventory inventory;
    private EntityPlayer player;

    public ForgeGuiContainer(ForgeContainer inventorySlotsIn, EntityPlayer player) {
        super(inventorySlotsIn);
        this.player = player;
        this.xSize = 189;
        this.ySize = 196;

        inventory = inventorySlotsIn.getHandler();
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

        IFluidTank tank = inventory.getCurrentDna();
        if (tank.getFluidAmount() > 0) {
            int currentHeight = (int) MathHelper.clamp((double) tank.getFluidAmount() / tank.getCapacity() * dnaFillingRect.height, 1, dnaFillingRect.height);
            this.drawTexturedModalRect(i + dnaFillingRect.x, j + dnaFillingRect.y + dnaFillingRect.height - currentHeight, xSize, dnaFillingRectOut.height - currentHeight, dnaFillingRectOut.width, currentHeight);
        }

        ITextComponent text = new TextComponentTranslation("divineadditions.gui.knowlegde_level");
        int currentLevel = inventory.getCurrentLevel();

        if (currentLevel > 0) {
            text.appendText(" ").appendSibling(new TextComponentTranslation("enchantment.level." + currentLevel));
        } else {
            text.appendText(" ?");
        }

        fontRenderer.drawSplitString(text.getFormattedText(), guiLeft + 129, guiTop + 10, 53, 10526880);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {

        int x = mouseX - this.guiLeft;
        int y = mouseY - this.guiTop;

        if (dnaRectangle.contains(x, y)) {
            TextComponentTranslation components = new TextComponentTranslation("divineadditions.tooltip.dna",
                    new DecimalFormat().format(inventory.getCurrentDna().getFluidAmount()),
                    new DecimalFormat().format(inventory.getCurrentDna().getCapacity()));
            drawHoveringText(components.getFormattedText(), x, y);
        }

        if (cageMobRect.contains(x, y)) {
            Slot slot = getSlotUnderMouse();
            if (slot == null || !slot.getHasStack()) {
                TextComponentTranslation components = new TextComponentTranslation("item.caged_mob.name");
                drawHoveringText(components.getFormattedText(), x, y);
            }
        }
    }
}
