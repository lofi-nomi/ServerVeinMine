package digital.naomie;

import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.lib.sat4j.core.VecInt;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Adapted from 
// https://raw.githubusercontent.com/feandrad/ServerVeinMine/5603dad46af9ce73d60a3b9776fb1b33a3d7aa6d/src/main/java/digital/naomie/VeinMineConfig.java
public class VeinMineConfig {
    public static final Logger LOGGER = LoggerFactory.getLogger("serverveinmine");
    public boolean generateConfigJSON = false;
    public boolean ShiftToActivate = true;
    public int maxBlocks = 64;
    public ArrayList<String> veinMineableBlocks = new ArrayList<>(
        List.of(
            Registries.BLOCK.getId(Blocks.COAL_ORE).toString(),
            Registries.BLOCK.getId(Blocks.IRON_ORE).toString(),
            Registries.BLOCK.getId(Blocks.GOLD_ORE).toString(),
            Registries.BLOCK.getId(Blocks.DIAMOND_ORE).toString(),
            Registries.BLOCK.getId(Blocks.EMERALD_ORE).toString(),
            Registries.BLOCK.getId(Blocks.LAPIS_ORE).toString(),
            Registries.BLOCK.getId(Blocks.REDSTONE_ORE).toString(),
            Registries.BLOCK.getId(Blocks.NETHER_QUARTZ_ORE).toString(),
            Registries.BLOCK.getId(Blocks.NETHER_GOLD_ORE).toString(),
            Registries.BLOCK.getId(Blocks.ANCIENT_DEBRIS).toString(),
            Registries.BLOCK.getId(Blocks.COPPER_ORE).toString(),
            Registries.BLOCK.getId(Blocks.DEEPSLATE_IRON_ORE).toString(),
            Registries.BLOCK.getId(Blocks.DEEPSLATE_GOLD_ORE).toString(),
            Registries.BLOCK.getId(Blocks.DEEPSLATE_COAL_ORE).toString(),
            Registries.BLOCK.getId(Blocks.DEEPSLATE_DIAMOND_ORE).toString(),
            Registries.BLOCK.getId(Blocks.DEEPSLATE_EMERALD_ORE).toString(),
            Registries.BLOCK.getId(Blocks.DEEPSLATE_LAPIS_ORE).toString(),
            Registries.BLOCK.getId(Blocks.DEEPSLATE_REDSTONE_ORE).toString(),
            Registries.BLOCK.getId(Blocks.DEEPSLATE_COPPER_ORE).toString(),
            Registries.BLOCK.getId(Blocks.GLOWSTONE).toString()
        ));
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("serverveinmine.config.json");

    public static VeinMineConfig loadConfig() {
        VeinMineConfig config = null;
        if (Files.exists(CONFIG_PATH)) {
            try (FileReader reader = new FileReader(CONFIG_PATH.toFile())) {
                config = GSON.fromJson(reader, VeinMineConfig.class);
                reader.close();
                LOGGER.info("Config file loaded");
                LOGGER.info(config.veinMineableBlocks.toString());
            } catch (IOException e) {
                LOGGER.error("Failed to load config file", e);
            }
        }else{
            LOGGER.info("Config file not found, generating default config");
            config = new VeinMineConfig();
            try (FileWriter writer = new FileWriter(CONFIG_PATH.toFile())) {
                GSON.toJson(config, writer);
                writer.close();
            } catch (JsonIOException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }
        return config;
    }
}

