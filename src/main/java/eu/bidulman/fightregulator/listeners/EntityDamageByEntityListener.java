package eu.bidulman.fightregulator.listeners;

import eu.bidulman.fightregulator.FightRegulator;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

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
            if (!plugin.getMode(player)) {
                if (!damager.hasPermission("fight-regulator.bypass")) {
                    event.setCancelled(true);
                    return;
                }
                damager.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(plugin.getConfigMessage("players-choose-mode.messages.bypass-a-disabled-player")));
            }
            if (!(plugin.getMode(damager) || damager.hasPermission("fight-regulator.bypass"))) {
                event.setCancelled(true);
                return;
            }
        }

        plugin.updateLastDamage(player);
        plugin.updateLastDamage(damager);
    }

}
