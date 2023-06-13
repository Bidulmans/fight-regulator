package eu.bidulaxstudio.fightregulator.listeners;

import eu.bidulaxstudio.fightregulator.FightRegulator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.Timestamp;
import java.time.Instant;

public class PlayerJoinListener implements Listener {
    private final FightRegulator plugin;

    public PlayerJoinListener(FightRegulator plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.lastJoin.put(event.getPlayer(), Timestamp.from(Instant.now()).getTime());
    }

}
