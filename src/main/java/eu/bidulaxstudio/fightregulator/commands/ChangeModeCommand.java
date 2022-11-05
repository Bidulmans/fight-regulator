package eu.bidulaxstudio.fightregulator.commands;

import eu.bidulaxstudio.fightregulator.FightRegulator;
import eu.bidulaxstudio.fightregulator.utils.PlayerSettings;
import eu.bidulaxstudio.fightregulator.utils.Time;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ChangeModeCommand implements CommandExecutor, TabCompleter {
    private final FightRegulator plugin;

    public ChangeModeCommand(FightRegulator plugin) {
        this.plugin = plugin;
        plugin.getCommand("pvp").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.sendConfigMessage(sender, "not-a-player");
            return true;
        }

        if (args.length < 1) {
            PlayerSettings playerSettings = plugin.getSettings().getPlayerSettings(player.getUniqueId());
            String mode;
            if (playerSettings == null) {
                mode = "default";
            } else {
                mode = playerSettings.mode;
            }

            player.sendMessage(plugin.getSettings().getMessage("player-mode").replace("{PLAYER}", player.getName()).replace("{MODE}", mode));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "on": {
                changeMode(player, "enabled");
                return true;
            }
            case "off": {
                changeMode(player, "disabled");
                return true;
            }
            case "default": {
                changeMode(player, "default");
                return true;
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> list = new ArrayList<>();

        if (args.length == 1 && sender instanceof Player player) {
            PlayerSettings playerSettings = plugin.getSettings().getPlayerSettings(player.getUniqueId());

            String mode;
            if (playerSettings == null) {
                mode = "default";
            } else {
                mode = playerSettings.mode;
            }

            if (!mode.equals("default")) list.add("default");
            if (!mode.equals("enabled")) list.add("on");
            if (!mode.equals("disabled")) list.add("off");
        }

        return list;
    }

    private void changeMode(Player player, String mode) {
        PlayerSettings playerSettings = plugin.getSettings().getPlayerSettings(player.getUniqueId());

        if (playerSettings == null) {
            plugin.setPlayerMode(player, mode, true);
            player.sendMessage(plugin.getSettings().getMessage("changed-mode").replace("{MODE}", mode));
        }

        else if (!playerSettings.mode.equals(mode)) {
            if (player.hasPermission("fightregulator.bypass-cooldown") || Time.getTime() >= playerSettings.lastChange + plugin.getSettings().getChangeModeCooldown()) {
                plugin.setPlayerMode(player, mode, true);
                player.sendMessage(plugin.getSettings().getMessage("changed-mode").replace("{MODE}", mode));
            }

            else {
                plugin.sendConfigMessage(player, "wait-cooldown");
            }
        }

        else {
            player.sendMessage(plugin.getSettings().getMessage("already-in-mode").replace("{MODE}", mode));
        }
    }

}
