package com.twicefear.diablosmp.listeners;

import com.twicefear.diablosmp.DiabloSMP;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;

public class PlayerJoinListener implements Listener {

    private final DiabloSMP plugin;
    private final NamespacedKey firstJoinKey;

    public PlayerJoinListener(DiabloSMP plugin) {
        this.plugin = plugin;
        this.firstJoinKey = new NamespacedKey(plugin, "first_join_done");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Show bossbar if SMP running
        plugin.getSmpManager().showBossBarTo(player);

        // True first join check using PersistentDataContainer
        if (!player.getPersistentDataContainer().has(firstJoinKey, PersistentDataType.BYTE)) {
            player.getPersistentDataContainer().set(firstJoinKey, PersistentDataType.BYTE, (byte) 1);
            plugin.getFirstJoinManager().startFirstJoinAnimation(player);
        }
    }
}
