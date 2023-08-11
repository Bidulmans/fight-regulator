package fr.bidulman.fightregulator.commands;

import fr.bidulman.fightregulator.FightRegulator;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainCommand implements CommandExecutor, TabCompleter {
    private final FightRegulator plugin;

    public MainCommand(FightRegulator plugin) {
        this.plugin = plugin;
        PluginCommand command = plugin.getCommand("fight-regulator");

        List<String> aliases = new ArrayList<>();
        aliases.add("pvp");

        command.setAliases(aliases);
        command.setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!plugin.getConfig().getBoolean("players-choose-mode.enabled")) {
            sender.sendMessage(plugin.getConfigMessage("players-choose-mode.messages.disabled"));
            return true;
        }

        if (args.length < 1) {
            if (!(sender instanceof Player player)) {
                return false;
            }

            if (plugin.getConfig().getStringList("players-choose-mode.excluded-worlds").contains(player.getWorld().getName())) {
                player.sendMessage(plugin.getConfigMessage("players-choose-mode.messages.disabled-world"));
                return true;
            }

            boolean mode = plugin.getMode(player);
            player.sendMessage(plugin.getConfigMessage("players-choose-mode.messages.display-mode", "mode", FightRegulator.modeToString(mode)));

            return true;
        }

        if (!(sender.hasPermission("fight-regulator.change"))) {
            return false;
        }

        boolean mode = args[0].equalsIgnoreCase("on");

        if (args.length == 1) {
            if (!(sender instanceof Player player)) {
                return false;
            }

            if (plugin.getConfig().getStringList("players-choose-mode.excluded-worlds").contains(player.getWorld().getName())) {
                player.sendMessage(plugin.getConfigMessage("players-choose-mode.messages.disabled-world"));
                return true;
            }

            if (mode == plugin.getMode(player)) {
                player.sendMessage(plugin.getConfigMessage("players-choose-mode.messages.already-mode", "player", player.getName(), "mode", FightRegulator.modeToString(mode)));
                return true;
            }

            if (!(mode || player.hasPermission("fight-regulator.bypass"))) {
                final long cooldown = plugin.getConfig().getLong("players-choose-mode.disable-cooldown") * 1000;
                final long now = FightRegulator.getTime();

                final long damageCooldown = now - plugin.getLastDamage(player);
                final long joinCooldown = now - plugin.getLastJoin(player);

                if (damageCooldown < cooldown || joinCooldown < cooldown) {
                    int waitingTime = (int) Math.ceil((cooldown - Math.min(damageCooldown, joinCooldown)) / 60000f);
                    player.sendMessage(plugin.getConfigMessage("players-choose-mode.messages.cant-disable", "time", String.valueOf(waitingTime)));
                    return true;
                }
            }

            plugin.setMode(player, mode);
            player.sendMessage(plugin.getConfigMessage("players-choose-mode.messages.changed-mode", "player", player.getName(), "mode", FightRegulator.modeToString(mode)));
            return true;
        }

        if (!(sender.hasPermission("fight-regulator.change-others"))) {
            return false;
        }

        String playerName = args[1];
        if (plugin.getServer().getPlayer(playerName) == null) {
            sender.sendMessage(plugin.getConfigMessage("players-choose-mode.messages.no-player", "player", playerName));
            return true;
        }

        Player player = plugin.getServer().getPlayer(playerName);

        if (mode == plugin.getMode(player)) {
            sender.sendMessage(plugin.getConfigMessage("players-choose-mode.messages.already-mode", "player", player.getName(), "mode", FightRegulator.modeToString(mode)));
            return true;
        }

        plugin.setMode(player, mode);
        sender.sendMessage(plugin.getConfigMessage("players-choose-mode.messages.changed-mode", "player", player.getName(), "mode", FightRegulator.modeToString(mode)));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && ((sender instanceof Player && sender.hasPermission("fight-regulator.change")) || sender.hasPermission("fight-regulator.change-others"))) {
            List<String> list = new ArrayList<>();
            list.add("on");
            list.add("off");

            return list;
        } else if (args.length == 2 && sender.hasPermission("fight-regulator.change-others")) {
            return null;
        }

        return Collections.emptyList();
    }
}
