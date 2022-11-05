package eu.bidulaxstudio.fightregulator.commands;

import eu.bidulaxstudio.fightregulator.FightRegulator;
import eu.bidulaxstudio.fightregulator.utils.IncompleteWorldSettings;
import eu.bidulaxstudio.fightregulator.utils.PlayerSettings;
import eu.bidulaxstudio.fightregulator.utils.Time;
import eu.bidulaxstudio.fightregulator.utils.WorldSettings;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ManageCommand implements CommandExecutor, TabCompleter {
    private final FightRegulator plugin;

    public ManageCommand(FightRegulator plugin) {
        this.plugin = plugin;
        plugin.getCommand("manage-pvp").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "server": {
                if (!sender.hasPermission("fightregulator.manage-server")) {
                    plugin.sendConfigMessage(sender, "no-permission");
                    return true;
                }
                if (args.length < 3) {
                    return false;
                }

                String enablePvPString = args[1].toLowerCase();
                String enablePlayerChoiceString = args[2].toLowerCase();

                boolean enablePvP;
                if (enablePvPString.equals("true")) enablePvP = true;
                else if (enablePvPString.equals("false")) enablePvP = false;
                else return false;

                boolean enablePlayerChoice;
                if (enablePlayerChoiceString.equals("true")) enablePlayerChoice = true;
                else if (enablePlayerChoiceString.equals("false")) enablePlayerChoice = false;
                else return false;

                plugin.getSettings().setServerSettings(new WorldSettings(enablePvP, enablePlayerChoice));
                plugin.sendConfigMessage(sender, "changed-server");

                return true;
            }

            case "world": {
                if (!sender.hasPermission("fightregulator.manage-server")) {
                    plugin.sendConfigMessage(sender, "no-permission");
                    return true;
                }
                if (args.length < 4) {
                    return false;
                }

                String world = args[1];
                String enablePvP = args[2].toLowerCase();
                String enablePlayerChoice = args[3].toLowerCase();

                List<String> worldSettingsKeywords = getWorldSettingsKeywords();
                if (!(worldSettingsKeywords.contains(enablePvP) && worldSettingsKeywords.contains(enablePlayerChoice))) {
                    return false;
                }

                plugin.getSettings().setWorldSettings(world, new IncompleteWorldSettings(enablePvP, enablePlayerChoice));
                plugin.sendConfigMessage(sender, "changed-world");

                return true;
            }

            case "player": {
                if (!sender.hasPermission("fightregulator.manage-players")) {
                    plugin.sendConfigMessage(sender, "no-permission");
                    return true;
                }
                if (args.length < 3) {
                    return false;
                }

                String player = args[1];
                String action = args[2].toLowerCase();

                Player target = Bukkit.getPlayer(player);
                if (target == null) {
                    plugin.sendConfigMessage(sender, "not-connected");
                    return true;
                }

                switch (action) {
                    case "getmode": {
                        PlayerSettings playerSettings = plugin.getSettings().getPlayerSettings(target.getUniqueId());
                        String mode;
                        if (playerSettings == null) {
                            mode = "default";
                        } else {
                            mode = playerSettings.mode;
                        }

                        sender.sendMessage(plugin.getSettings().getMessage("player-mode").replace("{PLAYER}", target.getName()).replace("{MODE}", mode));
                        return true;
                    }

                    case "setmode": {
                        if (args.length < 4) {
                            return false;
                        }

                        String mode = args[3].toLowerCase();

                        List<String> playerModeKeywords = getPlayerModeKeywords();
                        if (!playerModeKeywords.contains(mode)) {
                            return false;
                        }

                        if (args.length > 4) {
                            String updateCooldownString = args[4].toLowerCase();
                            if (updateCooldownString.equals("true")) {
                                plugin.getSettings().setPlayerSettings(target.getUniqueId(), new PlayerSettings(mode, Time.getTime()));
                                return true;
                            }
                        }

                        PlayerSettings oldPlayerSettings = plugin.getSettings().getPlayerSettings(target.getUniqueId());
                        if (oldPlayerSettings == null) {
                            plugin.getSettings().setPlayerSettings(target.getUniqueId(), new PlayerSettings(mode, 0));
                        } else {
                            plugin.getSettings().setPlayerSettings(target.getUniqueId(), new PlayerSettings(mode, oldPlayerSettings.lastChange));
                        }

                        plugin.sendConfigMessage(sender, "changed-player-mode");
                        return true;
                    }

                    case "getcooldown": {
                        PlayerSettings playerSettings = plugin.getSettings().getPlayerSettings(target.getUniqueId());
                        long cooldown;
                        if (playerSettings == null) {
                            cooldown = 0;
                        } else {
                            cooldown = Math.floorDiv(playerSettings.lastChange + plugin.getSettings().getChangeModeCooldown() - Time.getTime(), 1000);
                            if (cooldown < 0) {
                                cooldown = 0;
                            }
                        }

                        sender.sendMessage(plugin.getSettings().getMessage("player-cooldown").replace("{PLAYER}", target.getName()).replace("{COOLDOWN}", String.valueOf(cooldown)));
                        return true;
                    }

                    case "resetcooldown": {
                        PlayerSettings oldPlayerSettings = plugin.getSettings().getPlayerSettings(target.getUniqueId());
                        if (oldPlayerSettings == null) {
                            plugin.getSettings().setPlayerSettings(target.getUniqueId(), new PlayerSettings("default", 0));
                        } else {
                            plugin.getSettings().setPlayerSettings(target.getUniqueId(), new PlayerSettings(oldPlayerSettings.mode, 0));
                        }

                        plugin.sendConfigMessage(sender, "reset-player-cooldown");
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> list = new ArrayList<>();

        switch (args.length) {
            case 1: {
                list.add("server");
                list.add("world");
                list.add("player");
                return list;
            }

            case 2: {
                switch (args[0].toLowerCase()) {
                    case "server": {
                        list.add("true");
                        list.add("false");
                        return list;
                    }

                    case "world": {
                        for (World world : Bukkit.getWorlds()) {
                            list.add(world.getName());
                        }
                        return list;
                    }

                    case "player": {
                        return null;
                    }
                }
            }

            case 3: {
                switch (args[0].toLowerCase()) {
                    case "server": {
                        list.add("true");
                        list.add("false");
                        return list;
                    }

                    case "world": {
                        return getWorldSettingsKeywords();
                    }

                    case "player": {
                        list.add("getmode");
                        list.add("setmode");
                        list.add("getcooldown");
                        list.add("resetcooldown");
                        return list;
                    }
                }
            }

            case 4: {
                switch (args[0].toLowerCase()) {
                    case "world": {
                        return getWorldSettingsKeywords();
                    }

                    case "player": {
                        if (args[2].equalsIgnoreCase("setmode")) {
                            return getPlayerModeKeywords();
                        }
                    }
                }
            }
        }

        return list;
    }

    public static List<String> getWorldSettingsKeywords() {
        List<String> keywords = new ArrayList<>();
        keywords.add("true");
        keywords.add("false");
        keywords.add("default");
        return keywords;
    }

    public static List<String> getPlayerModeKeywords() {
        List<String> keywords = new ArrayList<>();
        keywords.add("enabled");
        keywords.add("disabled");
        keywords.add("default");
        return keywords;
    }

}
