package divineadditions.event;

import divineadditions.DivineAdditions;
import divineadditions.api.EntityDamageSourceIndirectEnch;
import net.minecraft.init.Enchantments;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = DivineAdditions.MOD_ID)
public class LootingLevelEventHandler {

    @SubscribeEvent
    public static void handle(LootingLevelEvent event) {
        if (event.getDamageSource() instanceof EntityDamageSourceIndirectEnch) {
            event.setLootingLevel(((EntityDamageSourceIndirectEnch) event.getDamageSource()).getLevel(Enchantments.LOOTING));
        }
    }
}
