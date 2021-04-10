package divineadditions.event;

import divineadditions.DivineAdditions;
import divineadditions.recipe.ISpecialRecipe;
import divineadditions.recipe.SpecialShaped;
import divineadditions.recipe.ingredient.RemainingIngredient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@Mod.EventBusSubscriber(modid = DivineAdditions.MOD_ID)
public class ItemCraftedEventHandler {

    @SubscribeEvent
    public static void handle(PlayerEvent.ItemCraftedEvent event) {
        IInventory craftMatrix = event.craftMatrix;
        EntityPlayer player = event.player;

        if (craftMatrix instanceof InventoryCrafting) {
            IRecipe recipe = CraftingManager.findMatchingRecipe(((InventoryCrafting) craftMatrix), player.world);
            if (recipe instanceof ISpecialRecipe) {
                for (RemainingIngredient remainingIngredient : ((SpecialShaped) recipe).getRemaining()) {
                    if (remainingIngredient.damage > 0) {
                        for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
                            ItemStack slot = craftMatrix.getStackInSlot(i);
                            if (remainingIngredient.apply(slot)) {
                                slot.damageItem(remainingIngredient.damage, player);
                            }
                        }
                    }
                }
            }
        }
    }
}
