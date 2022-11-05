package eu.bidulaxstudio.fightregulator.listeners;

import eu.bidulaxstudio.fightregulator.FightRegulator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageListener implements Listener {
    private final FightRegulator plugin;

    public EntityDamageListener(FightRegulator plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player damager)) {
            return;
        }
        if (!(event.getEntity() instanceof Player damaged)) {
            return;
        }
        if (plugin.canDamage(damager, damaged)) {
            return;
        }

        plugin.sendActionBarConfigMessage(damager, "pvp-prohibited");
        event.setCancelled(true);
    }

}
