package divineadditions.registry;

import divineadditions.DivineAdditions;
import divineadditions.entity.EntityCageBullet;
import divineadditions.holders.Tabs;
import divineadditions.item.ItemArmorEssence;
import divineadditions.item.ItemEntityBullet;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.function.Function;

@Mod.EventBusSubscriber(modid = DivineAdditions.MOD_ID)
public class EntityRegistryHandler {
    /**
     * Special index for projectiles
     */
    private static int projectileID = 0;

    @SubscribeEvent
    public static void registerItems(final RegistryEvent.Register<EntityEntry> event) {
        IForgeRegistry<EntityEntry> registry = event.getRegistry();

        registry.register(buildProjectile(EntityCageBullet.class, EntityCageBullet::new, "entity_cage_bullet"));
    }

    private static <T extends Entity> EntityEntry buildProjectile(Class<T> entityClass, Function<World, T> func, String entityID) {
        ResourceLocation id = new ResourceLocation(DivineAdditions.MOD_ID, entityID);

        return EntityEntryBuilder
                .create()
                .entity(entityClass)
                .id(id, projectileID++)
                .name(id.toString().replace(':', '.'))
                .tracker(250, 5, true)
                .factory(func::apply)
                .build();
    }
}
