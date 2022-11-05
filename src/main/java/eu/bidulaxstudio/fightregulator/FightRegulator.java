package eu.bidulaxstudio.fightregulator;

import eu.bidulaxstudio.fightregulator.commands.ChangeModeCommand;
import eu.bidulaxstudio.fightregulator.commands.ManageCommand;
import eu.bidulaxstudio.fightregulator.listeners.EntityDamageListener;
import eu.bidulaxstudio.fightregulator.utils.PlayerSettings;
import eu.bidulaxstudio.fightregulator.utils.Settings;
import eu.bidulaxstudio.fightregulator.utils.WorldSettings;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class FightRegulator extends JavaPlugin {
    private Settings settings;

    @Override
    public void onEnable() {
        loadSettings();
        loadCommands();
        loadListeners();
    }

    private void loadSettings() {
        settings = new Settings(this);
    }

    private void loadCommands() {
        new ManageCommand(this);
        new ChangeModeCommand(this);
    }

    private void loadListeners() {
        new EntityDamageListener(this);
    }

    public Settings getSettings() {
        return settings;
    }

    public void sendMessage(CommandSender target, String message) {
        target.sendMessage(message);
    }

    public void sendConfigMessage(CommandSender target, String path) {
        sendMessage(target, settings.getMessage(path));
    }

    public void sendActionBarMessage(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }

    public void sendActionBarConfigMessage(Player player, String path) {
        sendActionBarMessage(player, settings.getMessage(path));
    }

    public void setPlayerMode(Player player, String mode, boolean updateTime) {
        PlayerSettings playerSettings;
        if (updateTime) {
            playerSettings = new PlayerSettings(mode);
        } else {
            PlayerSettings oldPlayerSettings = settings.getPlayerSettings(player.getUniqueId());
            if (oldPlayerSettings == null) {
                playerSettings = new PlayerSettings(mode, 0);
            } else {
                playerSettings = new PlayerSettings(mode, oldPlayerSettings.lastChange);
            }
        }

        settings.setPlayerSettings(player.getUniqueId(), playerSettings);
    }

    public boolean canDamage(Player damager, Player damaged) {
        World world = damager.getWorld();
        WorldSettings worldSettings = settings.getWorldSettings(world.getName());

        boolean damagerHasEnabledPvP = settings.hasEnabledPvP(damager.getUniqueId(), worldSettings.enablePvP);
        boolean damagedHasEnabledPvP = settings.hasEnabledPvP(damaged.getUniqueId(), worldSettings.enablePvP);

        if (worldSettings.enablePlayerChoice) {
            return damagerHasEnabledPvP && damagedHasEnabledPvP;
        } else {
            return worldSettings.enablePvP;
        }
    }

}
