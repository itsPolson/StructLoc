package io.github.paulmrtnz;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class Config {
    private static FileConfiguration config;
    private static File configFile;

    public static void load(Plugin plugin) {
        configFile = new File(plugin.getDataFolder(), "config.yml");

        // Créer le dossier du plugin s'il n'existe pas
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        // Si le fichier n'existe pas, créer un par défaut
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }

        // Charger la configuration
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public static int getMaxSearchDistance() {
        return config.getInt("search.max-radius", 20000);
    }

    public static boolean shouldFindUnexplored() {
        return config.getBoolean("search.find-unexplored", false);
    }

    public static long getCompassUpdateInterval() {
        return config.getLong("compass.update-interval-ticks", 5);
    }

    public static String getCompassName() {
        return config.getString("compass.name", "Localiseur de structure");
    }
}
