package com.diablosmp.plugin.listener;

import com.diablosmp.plugin.DiabloSMP;
import com.diablosmp.plugin.gui.StarterGUI;
import com.diablosmp.plugin.model.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {
    private final DiabloSMP plugin;

    public PlayerConnectionListener(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerData data = plugin.getStorageService().getPlayerData(player.getUniqueId());

        plugin.getHudService().updateHud(player);

        // First join starter check
        if (!data.isFirstJoinClaimed() && plugin.getConfig().getBoolean("first-join.enabled", true)) {
            if (player.hasPermission("diablosmp.receive.firstjoin")) {
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    if (player.isOnline() && !data.isFirstJoinClaimed()) {
                        StarterGUI.openStarterMenu(plugin, player);
                    }
                }, 20L);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getHudService().removePlayer(player.getUniqueId());
        plugin.getStorageService().unloadPlayerData(player.getUniqueId());
    }
}
