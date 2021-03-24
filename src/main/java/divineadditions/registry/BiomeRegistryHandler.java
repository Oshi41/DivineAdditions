package divineadditions.registry;

import divineadditions.DivineAdditions;
import divineadditions.world.dimension.planet.PlanetBiome;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = DivineAdditions.MOD_ID)
public class BiomeRegistryHandler {

    @SubscribeEvent()
    public static void registerBiomes(final RegistryEvent.Register<Biome> event) {
        IForgeRegistry<Biome> registry = event.getRegistry();

        Biome planet = register(registry, new PlanetBiome(), "planet", BiomeDictionary.Type.END);
        BiomeManager.addBiome(BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(planet, 10));
    }

    private static Biome register(IForgeRegistry<Biome> registry, Biome biome, String name, BiomeDictionary.Type... types) {
        registry.register(biome.setRegistryName(DivineAdditions.MOD_ID, name));

        if (types != null && types.length > 0)
            BiomeDictionary.addTypes(biome, types);

        return biome;
    }
}
