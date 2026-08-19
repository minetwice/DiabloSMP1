package com.twicefear.diablosmp.listeners;

import com.twicefear.diablosmp.DiabloSMP;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final DiabloSMP plugin;

    public PlayerJoinListener(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        // Show bossbar if SMP running
        plugin.getSmpManager().showBossBarTo(event.getPlayer());

        // First join animation (check if first time via player data later - simple version now)
        // For demo: only if they have no stone absorbed and inventory empty of stones
        if (!plugin.getAbsorbManager().hasAbsorbed(event.getPlayer())) {
            // You can add a persistent data check later for true first join
            // For now we trigger on every join without absorbed stone (can be improved)
            // plugin.getFirstJoinManager().startFirstJoinAnimation(event.getPlayer());
        }
    }
}
