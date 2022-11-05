package eu.bidulaxstudio.fightregulator.utils;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.bidulaxstudio.fightregulator.FightRegulator;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Settings {
    public static final String playerSettingsFile = "player-settings.json";

    private final FightRegulator plugin;
    private Configuration config;

    private WorldSettings serverSettings;
    private final Map<String, IncompleteWorldSettings> worldSettings = new HashMap<>();
    private final Map<UUID, PlayerSettings> playerSettings = new HashMap<>();

    private long changeModeCooldown;

    public Settings(FightRegulator plugin) {
        this.plugin = plugin;

        loadConfig();
        loadPlayerSettings();
    }

    private void loadConfig() {
        plugin.saveDefaultConfig();
        this.config = plugin.getConfig();
        changeModeCooldown = config.getLong("change-mode-cooldown");
        loadServerSettings();
        loadWorldSettings();
    }

    private void loadServerSettings() {
        serverSettings = getWorldSettingsFromConfig("server-settings");
    }

    private void loadWorldSettings() {
        ConfigurationSection worldSettingsSection = config.getConfigurationSection("world-settings");
        for (String key : worldSettingsSection.getKeys(false)) {
            worldSettings.put(key, getIncompleteWorldSettingsFromConfig("world-settings." + key));
        }
    }

    private WorldSettings getWorldSettingsFromIncomplete(IncompleteWorldSettings incompleteSettings, boolean defaultEnablePvP, boolean defaultEnablePlayerChoice) {
        boolean enablePvP;
        if (incompleteSettings.enablePvP.equals("default")) {
            enablePvP = defaultEnablePvP;
        } else {
            enablePvP = incompleteSettings.enablePvP.equals("true");
        }

        boolean enablePlayerChoice;

        if (incompleteSettings.enablePlayerChoice.equals("default")) {
            enablePlayerChoice = defaultEnablePlayerChoice;
        } else {
            enablePlayerChoice = incompleteSettings.enablePlayerChoice.equals("true");
        }

        return new WorldSettings(enablePvP, enablePlayerChoice);
    }

    private IncompleteWorldSettings getIncompleteWorldSettingsFromConfig(String path) {
        String enablePvPPath = path + ".enable-pvp";
        String enablePvP;

        if (config.get(enablePvPPath) == null) {
            enablePvP = "default";
        } else {
            enablePvP = config.getString(enablePvPPath);
        }

        String enablePlayerChoicePath = path + ".enable-player-choice";
        String enablePlayerChoice;

        if (config.get(enablePlayerChoicePath) == null) {
            enablePlayerChoice = "default";
        } else {
            enablePlayerChoice = config.getString(enablePlayerChoicePath);
        }

        return new IncompleteWorldSettings(enablePvP, enablePlayerChoice);
    }

    private WorldSettings getWorldSettingsFromConfig(String path) {
        return getWorldSettingsFromIncomplete(getIncompleteWorldSettingsFromConfig(path), true, false);
    }

    private void loadPlayerSettings() {
        File file = new File(plugin.getDataFolder(), playerSettingsFile);

        if (file.exists()) {
            Gson gson = new Gson();

            try (Reader reader = new FileReader(file)) {
                Type type = new TypeToken<Map<String, PlayerSettings>>() {}.getType();
                Map<String, PlayerSettings> data = gson.fromJson(reader, type);

                for (Map.Entry<String, PlayerSettings> entry : data.entrySet()) {
                    UUID uuid = UUID.fromString(entry.getKey());
                    PlayerSettings settings = entry.getValue();

                    playerSettings.put(uuid, settings);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        plugin.getLogger().info("Loaded " + playerSettingsFile);
    }

    private void savePlayerSettings() {
        synchronized (playerSettings) {
            File file = new File(plugin.getDataFolder(), playerSettingsFile);

            Map<String, PlayerSettings> data = new HashMap<>();

            for (Map.Entry<UUID, PlayerSettings> entry : playerSettings.entrySet()) {
                data.put(entry.getKey().toString(), entry.getValue());
            }

            try (Writer writer = new FileWriter(file)) {
                Gson gson = new GsonBuilder().create();
                gson.toJson(data, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        plugin.getLogger().info("Saved " + playerSettingsFile);
    }

    public void setServerSettings(WorldSettings settings) {
        serverSettings = settings;
        plugin.getConfig().set("server-settings.enable-pvp", settings.enablePvP);
        plugin.getConfig().set("server-settings.enable-player-choice", settings.enablePlayerChoice);
        plugin.saveConfig();
    }

    public WorldSettings getWorldSettings(String world) {
        IncompleteWorldSettings incompleteSettings = worldSettings.get(world);
        if (incompleteSettings == null) {
            return serverSettings;
        }

        return getWorldSettingsFromIncomplete(incompleteSettings, serverSettings.enablePvP, serverSettings.enablePlayerChoice);
    }

    public void setWorldSettings(String world, IncompleteWorldSettings settings) {
        worldSettings.put(world, settings);
        plugin.getConfig().set("world-settings." + world + ".enable-pvp", settings.enablePvP);
        plugin.getConfig().set("world-settings." + world + ".enable-player-choice", settings.enablePlayerChoice);
        plugin.saveConfig();
    }

    public PlayerSettings getPlayerSettings(UUID player) {
        return playerSettings.get(player);
    }

    public void setPlayerSettings(UUID player, PlayerSettings settings) {
        playerSettings.put(player, settings);
        savePlayerSettings();
    }

    public boolean hasEnabledPvP(UUID player, boolean defaultValue) {
        PlayerSettings playerSettings = getPlayerSettings(player);
        boolean playerHasEnabledPvP;
        if (playerSettings == null || playerSettings.mode.equals("default")) {
            playerHasEnabledPvP = defaultValue;
        } else {
            playerHasEnabledPvP = playerSettings.mode.equals("enabled");
        }
        return playerHasEnabledPvP;
    }

    public long getChangeModeCooldown() {
        return changeModeCooldown * 1000;
    }

    public String getMessage(String message) {
        return config.getString("messages." + message);
    }

}
