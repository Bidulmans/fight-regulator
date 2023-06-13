package eu.bidulaxstudio.fightregulator.listeners;

import eu.bidulaxstudio.fightregulator.FightRegulator;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.sql.Timestamp;
import java.time.Instant;

public class EntityDamageByEntityListener implements Listener {
    private final FightRegulator plugin;

    public EntityDamageByEntityListener(FightRegulator plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (!(event.getDamager() instanceof Player damager)) {
            return;
        }

        if (!(plugin.getConfig().getStringList("players-choose-mode.excluded-worlds").contains(player.getWorld().getName()))) {
            if (!plugin.playerSettings.getOrDefault(player.getUniqueId().toString(), false)) {
                if (!damager.hasPermission("fight-regulator.bypass")) {
                    event.setCancelled(true);
                    return;
                }
                damager.spigot().sendMessage(TextComponent.fromLegacyText(plugin.getConfigMessage("players-choose-mode.messages.bypass-a-disabled-player")));
            }
            if (!(plugin.playerSettings.getOrDefault(damager.getUniqueId().toString(), false) || damager.hasPermission("fight-regulator.bypass"))) {
                event.setCancelled(true);
                return;
            }
        }

        plugin.lastDamage.put(player, Timestamp.from(Instant.now()).getTime());
        plugin.lastDamage.put(damager, Timestamp.from(Instant.now()).getTime());
    }

}
