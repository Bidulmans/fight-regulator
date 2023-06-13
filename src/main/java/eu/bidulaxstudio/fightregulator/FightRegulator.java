package eu.bidulaxstudio.fightregulator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import eu.bidulaxstudio.fightregulator.commands.MainCommand;
import eu.bidulaxstudio.fightregulator.listeners.EntityDamageByEntityListener;
import eu.bidulaxstudio.fightregulator.listeners.PlayerJoinListener;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class FightRegulator extends JavaPlugin implements CommandExecutor, TabCompleter, Listener {
    public final Map<Player, Long> lastDamage = new HashMap<>();
    public final Map<Player, Long> lastJoin = new HashMap<>();
    public final Map<String, Boolean> playerSettings = new HashMap<>();

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

    private void loadCommands() {
        new MainCommand(this);
    }

    private void loadListeners() {
        getServer().getPluginManager().registerEvents(new EntityDamageByEntityListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
    }

    private void loadOnlinePlayers() {
        for (Player player : getServer().getOnlinePlayers()) {
            lastJoin.put(player, Timestamp.from(Instant.now()).getTime());
        }
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

    public void savePlayerSettings() {
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

    public String getConfigMessage(String path, String... replacements) {
        String message = getConfig().getString(path);
        for (int i = 0; i < replacements.length; i+=2) {
            message = message.replace("{" + replacements[i].toLowerCase() + "}", replacements[i+1]);
        }

        return message;
    }

}
