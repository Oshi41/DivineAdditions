package divineadditions.gui.conainter;

import divineadditions.api.IForgeInventory;
import divineadditions.gui.CraftingSlot;
import divineadditions.gui.conainter.base.ContainerItemHandler;
import divineadditions.recipe.ForgeRecipes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ForgeContainer extends ContainerItemHandler {
    private final static Set<ForgeRecipes> recipes = ForgeRegistries
            .RECIPES
            .getValuesCollection()
            .stream()
            .filter(x -> x instanceof ForgeRecipes)
            .map(x -> ((ForgeRecipes) x))
            .collect(Collectors.toSet());

    public InventoryCraftResult craftResult;
    public InventoryCrafting matrix;
    private IForgeInventory handler;
    private EntityPlayer player;
    private CraftingSlot craftingSlot;

    public ForgeContainer(IForgeInventory handler, EntityPlayer player) {
        super(handler.getCurrentHandler(), player);
        this.handler = handler;
        this.player = player;
        this.matrix = new InventoryCrafting(this, handler.getWidth(), handler.getHeight());
        this.craftResult = new InventoryCraftResult();

        drawSlots();
        handler.openInventory(player);
    }

    @Nullable
    public static ForgeRecipes findFromResult(ItemStack result) {
        return recipes.stream().filter(x -> x.getRecipeOutput().equals(result)).findFirst().orElse(null);
    }

    protected void drawSlots() {
        int i = 0;

        while (i < matrix.getSizeInventory()) {
            int x = i % matrix.getWidth();
            int y = i / matrix.getHeight();

            Slot slot = new Slot(matrix, i++, 30 + x * 18, 12 + y * 18);
            this.addSlotToContainer(slot);
        }

        // cage mobs
        this.addSlotToContainer(new SlotItemHandler(handler.getCurrentHandler(), 0, 5, 84));

        // output
        craftingSlot = new CraftingSlot(player, craftResult, handler, 0, 156, 52);
        this.addSlotToContainer(craftingSlot);

        Map<TileEntity, IItemHandler> handlerCatalystStands = handler.findCatalystStands();
        int amount = 0;

        for (IItemHandler itemHandler : handlerCatalystStands.values()) {
            for (i = 0; i < itemHandler.getSlots(); i++) {
                SlotItemHandler slot = new SlotItemHandler(itemHandler, i, 195, 3 + amount * 18) {
                    @Override
                    public boolean canTakeStack(EntityPlayer playerIn) {
                        return false;
                    }

                    @Override
                    public boolean isItemValid(@Nonnull ItemStack stack) {
                        return false;
                    }
                };
                addSlotToContainer(slot);
                amount++;
            }
        }

        this.inventoryEnd = this.inventorySlots.size();

        super.drawPlayerSlots(player, 113, 172, 14);
    }

    @Override
    protected void drawHandlerSlots(IItemHandler handler) {
        // ignored
    }

    @Override
    protected void drawPlayerSlots(EntityPlayer player, int topSlotHeight, int hotbarHeight, int xStart) {
        // ignored
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return handler.canInteractWith(playerIn);
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        slotChangedCraftingGrid(player.world, player, handler, craftResult);
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        handler.closeInventory(player);
        clearContainer(playerIn, playerIn.getEntityWorld(), matrix);
        super.onContainerClosed(playerIn);
    }

    public IForgeInventory getHandler() {
        return handler;
    }

    protected void slotChangedCraftingGrid(World world, EntityPlayer player, IForgeInventory inventory, InventoryCraftResult inventoryCraftResult) {
        if (!world.isRemote) {
            EntityPlayerMP entityplayermp = (EntityPlayerMP) player;
            ItemStack itemstack = ItemStack.EMPTY;
            ForgeRecipes irecipe = recipes.stream().filter(x -> x.matches(matrix, world)).findFirst().orElse(null);

            if (irecipe != null && (irecipe.isDynamic() || !world.getGameRules().getBoolean("doLimitedCrafting") || entityplayermp.getRecipeBook().isUnlocked(irecipe))) {
                inventoryCraftResult.setRecipeUsed(irecipe);
                itemstack = irecipe.getCraftingResult(inventory);
            }

            craftingSlot.setCurrentRecipe(irecipe);
            inventoryCraftResult.setInventorySlotContents(0, itemstack);
            entityplayermp.connection.sendPacket(new SPacketSetSlot(this.windowId, craftingSlot.slotNumber, itemstack));
        }
    }
}
