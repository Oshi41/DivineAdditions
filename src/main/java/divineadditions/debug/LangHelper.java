package divineadditions.debug;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import openmods.reflection.ReflectionHelper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;

public class LangHelper {
    private String modId;
    private final Map<File, Collection<String>> langFiles = new HashMap<>();

    public LangHelper(String modId) {
        this.modId = modId;
        File langFolder = Paths.get(System.getProperty("user.dir"),
                "../", "src", "main", "resources", "assets", modId, "lang").toFile();

        for (File file : langFolder.listFiles()) {
            try {
                List<String> content = FileUtils.readLines(file, StandardCharsets.UTF_8);
                langFiles.put(file, content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void fill() {
        Set<ResourceLocation> notTranslated = new HashSet<>(findNotTranslated(ForgeRegistries.ENTITIES, "entity"));
        notTranslated.addAll(findNotTranslated(ForgeRegistries.ITEMS, "item"));
        notTranslated.addAll(findNotTranslated(ForgeRegistries.BLOCKS, "block"));
        notTranslated.addAll(findNotTranslated(ForgeRegistries.BIOMES, "biome"));

        try {
            RegistryNamespaced<ResourceLocation, Class<? extends TileEntity>> tileRegistry = (RegistryNamespaced<ResourceLocation, Class<? extends TileEntity>>) ReflectionHelper.getField(TileEntity.class, "REGISTRY").get(null);
            notTranslated.addAll(findNotTranslated(tileRegistry.getKeys(), "tile"));

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if (notTranslated.isEmpty())
            return;

        System.out.println(String.format("Founded %s missing translations:", notTranslated.size()));
        notTranslated.forEach(location -> System.out.println(location.toString()));

        langFiles.forEach((file, list) -> {
            notTranslated.forEach(location -> {
                list.add(String.format("%s.%s.name=%s",
                        location.getResourceDomain(),
                        location.getResourcePath(),
                        location.getResourcePath()));
            });

            try {
                FileUtils.writeLines(file, list);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private List<ResourceLocation> findNotTranslated(IForgeRegistry registry, String prefix) {
        return findNotTranslated(registry.getKeys(), prefix);
    }

    private List<ResourceLocation> findNotTranslated(Set<ResourceLocation> entries, String prefix) {
        ArrayList<ResourceLocation> result = new ArrayList<>();

        for (Map.Entry<File, Collection<String>> entry : langFiles.entrySet()) {

            for (ResourceLocation id : entries) {
                if (!id.getResourceDomain().equals(modId))
                    continue;

                String key = String.format("%s.%s.name", prefix, id.toString());

                if (entry.getValue().stream().anyMatch(x -> x.contains(key))) {
                    continue;
                }

                result.add(new ResourceLocation(prefix, id.getResourcePath()));
            }
        }

        return result;
    }

}
