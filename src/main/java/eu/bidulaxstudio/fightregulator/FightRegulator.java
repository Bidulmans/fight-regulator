package eu.bidulaxstudio.fightregulator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import eu.bidulaxstudio.fightregulator.commands.MainCommand;
import eu.bidulaxstudio.fightregulator.listeners.EntityDamageByEntityListener;
import eu.bidulaxstudio.fightregulator.listeners.PlayerJoinListener;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class FightRegulator extends JavaPlugin implements CommandExecutor, TabCompleter, Listener {
    private final Map<Player, Long> lastDamage = new HashMap<>();
    private final Map<Player, Long> lastJoin = new HashMap<>();
    private final Map<String, Boolean> playerSettings = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadPlayerSettings();
        loadOnlinePlayers();
        loadCommands();
        loadListeners();
    }

    @Override
    public void onDisable() {
        savePlayerSettings();
    }

    private void loadPlayerSettings() {
        String playerSettingsFile = getConfig().getString("players-choose-mode.file");
        File file = new File(getDataFolder(), playerSettingsFile);

        if (!file.exists()) {
            return;
        }

        Gson gson = new Gson();

        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<String, Boolean>>() {}.getType();
            playerSettings.putAll(gson.fromJson(reader, type));
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        getLogger().info("Loaded " + playerSettingsFile);
    }

    private void savePlayerSettings() {
        String playerSettingsFile = getConfig().getString("players-choose-mode.file");

        synchronized (playerSettings) {
            File file = new File(getDataFolder(), playerSettingsFile);

            try (Writer writer = new FileWriter(file)) {
                Gson gson = new GsonBuilder().create();
                gson.toJson(playerSettings, writer);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        getLogger().info("Saved " + playerSettingsFile);
    }

    private void loadOnlinePlayers() {
        for (Player player : getServer().getOnlinePlayers()) {
            updateLastJoin(player);
        }
    }

    private void loadCommands() {
        new MainCommand(this);
    }

    private void loadListeners() {
        getServer().getPluginManager().registerEvents(new EntityDamageByEntityListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
    }

    public Long getLastDamage(Player player) {
        return lastDamage.getOrDefault(player, 0L);
    }

    public void updateLastDamage(Player player) {
        lastDamage.put(player, getTime());
    }

    public Long getLastJoin(Player player) {
        return lastJoin.get(player);
    }

    public void updateLastJoin(Player player) {
        lastJoin.put(player, getTime());
    }

    public boolean getMode(Player player) {
        return playerSettings.getOrDefault(player.getUniqueId().toString(), false);
    }

    public void setMode(Player player, boolean mode) {
        playerSettings.put(player.getUniqueId().toString(), mode);
        if (getConfig().getBoolean("players-choose-mode.save-on-change")) {
            savePlayerSettings();
        }

        logModeChange(player, mode);
    }

    public void logModeChange(Player player, boolean mode) {
        final String message = "PvP mode set to " + modeToString(mode) + " for " + player.getName() + ".";
        if (getConfig().getBoolean("players-choose-mode.coreprotect-log")) {
            CoreProtectAPI coreProtect = getCoreProtect();
            if (coreProtect != null) {
                coreProtect.logChat(player, message);
            } else {
                getLogger().warning("CoreProtect logging is enabled but plugin is not usable.");
            }
        }
        getLogger().info(message);
    }

    private CoreProtectAPI getCoreProtect() {
        Plugin plugin = getServer().getPluginManager().getPlugin("CoreProtect");

        if (!(plugin instanceof CoreProtect)) {
            return null;
        }

        CoreProtectAPI coreProtect = ((CoreProtect) plugin).getAPI();
        if (!coreProtect.isEnabled()) {
            return null;
        }

        if (coreProtect.APIVersion() < 9) {
            return null;
        }

        return coreProtect;
    }

    public String getConfigMessage(String path, String... replacements) {
        String message = getConfig().getString(path);
        for (int i = 0; i < replacements.length; i+=2) {
            message = message.replace("{" + replacements[i].toLowerCase() + "}", replacements[i+1]);
        }

        return message;
    }

    public static Long getTime() {
        return Timestamp.from(Instant.now()).getTime();
    }

    public static String modeToString(boolean mode) {
        return mode ? "on" : "off";
    }

}
