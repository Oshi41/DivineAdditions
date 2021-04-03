package divineadditions.registry;

import divineadditions.DivineAdditions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.io.FilenameUtils;

@Mod.EventBusSubscriber
public class LootTableLoadEventHandler {

    public static void registerLootTables() {
        ModContainer container = Loader.instance().activeModContainer();

        loadLootTableFolder(container, "override");
        loadLootTableFolder(container, "entity");
        loadLootTableFolder(container, "chest");
    }

    private static void loadLootTableFolder(ModContainer container, final String folderName) {
        CraftingHelper.findFiles(
                container,
                "assets/" + DivineAdditions.MOD_ID + "/loot_tables/" + folderName,
                null,
                (root, file) -> {
                    String fileName = file.getFileName().toString();

                    if (fileName.endsWith(".json")) {
                        String nameWithoutExtension = FilenameUtils.getBaseName(fileName);
                        ResourceLocation location = new ResourceLocation(DivineAdditions.MOD_ID, folderName + "/" + nameWithoutExtension);
                        LootTableList.register(location);
                    }

                    return true;
                }, true, true
        );
    }

    /**
     * Name convention for pool names:
     * [MOD_ID]:[INDEX]
     * where MOD_ID - current mod name
     * where INDEX - non-negative integer starting from 0. No spacing between sequence!!!
     *
     * @param event
     */
    @SubscribeEvent
    public static void handle(LootTableLoadEvent event) {
        ResourceLocation id = event.getName();

        if (id.getResourcePath().contains("entities")) {
            ResourceLocation idToReplace = new ResourceLocation(DivineAdditions.MOD_ID, id.getResourcePath().replace("entities", "override"));

            if (LootTableList.getAll().contains(idToReplace)) {
                LootTable overrideTable = event.getLootTableManager().getLootTableFromLocation(idToReplace);
                if (overrideTable != null) {
                    for (int i = 0; true; i++) {
                        LootPool pool = overrideTable.getPool(idToReplace.getResourceDomain() + ":" + i);
                        if (pool == null)
                            break;

                        event.getTable().addPool(pool);
                    }
                }
            }
        }
    }
}
