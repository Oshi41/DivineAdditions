package divineadditions.registry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import divineadditions.DivineAdditions;
import divineadditions.config.DivineAdditionsConfig;
import divineadditions.config.PlanetConfig;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.common.Loader;
import org.apache.commons.io.FileUtils;
import scala.actors.threadpool.Arrays;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ConfigHandler {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .enableComplexMapKeySerialization()
            .create();

    public static void sync() {
        ConfigManager.sync(DivineAdditions.MOD_ID, Config.Type.INSTANCE);
        File planetsFile = new File(Loader.instance().getConfigDir(), DivineAdditions.MOD_ID + "/planets.json");

        if (!planetsFile.exists()) {
            try {
                planetsFile.createNewFile();
                String text = gson.toJson(DivineAdditionsConfig.planetDimensionConfig.possiblePlanets);
                FileUtils.write(planetsFile, text, StandardCharsets.UTF_8);
            } catch (IOException e) {
                DivineAdditions.logger.warn(e);
            }
        }

        String jsonRaw = "";

        try {
            jsonRaw = FileUtils.readFileToString(planetsFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        DivineAdditionsConfig.planetDimensionConfig.possiblePlanets = Arrays.asList(gson.fromJson(jsonRaw, PlanetConfig[].class));
    }
}
