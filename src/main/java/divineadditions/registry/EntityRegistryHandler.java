package divineadditions.registry;

import divineadditions.DivineAdditions;
import divineadditions.entity.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Function;

@Mod.EventBusSubscriber(modid = DivineAdditions.MOD_ID)
public class EntityRegistryHandler {
    /**
     * Special index for projectiles
     */
    private static int networkId = 0;

    @SubscribeEvent
    public static void registerItems(final RegistryEvent.Register<EntityEntry> event) {
        IForgeRegistry<EntityEntry> registry = event.getRegistry();

        registry.register(buildProjectile(EntityCageBullet.class, EntityCageBullet::new, "entity_cage_bullet"));
        registry.register(buildProjectile(EntityBullet.class, EntityBullet::new, "rifle_bullet"));

        registry.register(buildEntity(EntityArmorDefender.class, EntityArmorDefender::new, "armor_defender"));
        registry.register(buildEntity(EntityAncientVillager.class, EntityAncientVillager::new, "ancient_villager"));

        registry.register(buildEntityNoEgg(EntityDefenderStand.class, EntityDefenderStand::new, "defender_stand"));
    }

    private static <T extends Entity> EntityEntry buildEntity(Class<T> entityClass, Function<World, T> func, String entityID) {
        ResourceLocation id = new ResourceLocation(DivineAdditions.MOD_ID, entityID);

        return EntityEntryBuilder
                .create()
                .entity(entityClass)
                .id(id, networkId++)
                .name(id.toString())
                .tracker(128, 3, true)
                .factory(func::apply)
                .egg(0, Integer.MAX_VALUE)
                .build();
    }

    private static <T extends Entity> EntityEntry buildEntityNoEgg(Class<T> entityClass, Function<World, T> func, String entityID) {
        ResourceLocation id = new ResourceLocation(DivineAdditions.MOD_ID, entityID);

        return EntityEntryBuilder
                .create()
                .entity(entityClass)
                .id(id, networkId++)
                .name(id.toString())
                .tracker(128, 3, true)
                .factory(func::apply)
                .build();
    }

    private static <T extends Entity> EntityEntry buildProjectile(Class<T> entityClass, Function<World, T> func, String entityID) {
        ResourceLocation id = new ResourceLocation(DivineAdditions.MOD_ID, entityID);

        return EntityEntryBuilder
                .create()
                .entity(entityClass)
                .id(id, networkId++)
                .name(id.toString())
                .tracker(250, 5, true)
                .factory(func::apply)
                .build();
    }
}
