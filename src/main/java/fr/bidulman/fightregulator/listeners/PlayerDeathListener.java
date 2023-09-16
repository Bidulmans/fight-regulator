package fr.bidulman.fightregulator.listeners;

import fr.bidulman.fightregulator.FightRegulator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {
    private final FightRegulator plugin;

    public PlayerDeathListener(FightRegulator plugin) {
        this.plugin = plugin;
        if (plugin.getConfig().getBoolean("pvp-keepinventory.enabled")) {
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (player.getKiller() == null) {
            return;
        }
        if (player.getKiller() == player) {
            return;
        }
        if (plugin.getConfig().getStringList("pvp-keepinventory.excluded-worlds").contains(player.getWorld().getName())) {
            return;
        }

        event.setKeepInventory(true);
        event.getDrops().clear();
        
        if (plugin.getConfig().getBoolean("pvp-keepinventory.keep-levels")) {
            event.setKeepLevel(true);
            event.setDroppedExp(0);
        }
    }
}
