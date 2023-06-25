package fr.bidulman.fightregulator.listeners;

import fr.bidulman.fightregulator.FightRegulator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private final FightRegulator plugin;

    public PlayerJoinListener(FightRegulator plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.updateLastJoin(event.getPlayer());

        plugin.showPlayerMode(event.getPlayer(), plugin.getMode(event.getPlayer()));
    }

}
