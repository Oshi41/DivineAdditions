package divineadditions.event;

import divineadditions.DivineAdditions;
import divineadditions.recipe.InfusingRecipe;
import divineadditions.tile.TileEntityInfusingAltar;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.stream.StreamSupport;

@Mod.EventBusSubscriber(modid = DivineAdditions.MOD_ID)
public class InfusingAltarEvents {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void handle(EntityJoinWorldEvent e) {
        if (e.getEntity() instanceof EntityLightningBolt) {

            BlockPos position = e.getEntity().getPosition();
            TileEntityInfusingAltar entity = StreamSupport.stream(
                    BlockPos.getAllInBoxMutable(position.add(-1, -1, -1), position.add(1, 1, 1)).spliterator(),
                    false
            ).map(x -> e.getWorld().getTileEntity(x))
                    .filter(x -> x instanceof TileEntityInfusingAltar)
                    .map(x -> ((TileEntityInfusingAltar) x))
                    .findFirst()
                    .orElse(null);

            InfusingRecipe recipe = entity.findByCatalyst();
            if (recipe != null && recipe.type.equals("lightning")) {
                entity.tryInfuse(recipe);
            }
        }
    }
}
