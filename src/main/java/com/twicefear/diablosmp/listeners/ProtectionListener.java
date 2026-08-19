package com.twicefear.diablosmp.listeners;

import com.twicefear.diablosmp.DiabloSMP;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class ProtectionListener implements Listener {

    private final DiabloSMP plugin;

    public ProtectionListener(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof org.bukkit.entity.Player player)) return;
        if (plugin.getFirstJoinManager().isProtected(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (plugin.getStoneManager().isDiabloStone(event.getItemDrop().getItemStack())) {
            if (!plugin.getConfigManager().canStonesBeDropped()) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("§cYou cannot drop Diablo Stones!");
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (!plugin.getConfigManager().canStonesDropOnDeath()) {
            event.getDrops().removeIf(item -> plugin.getStoneManager().isDiabloStone(item));
        }
    }
}
