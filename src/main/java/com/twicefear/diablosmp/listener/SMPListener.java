package com.twicefear.diablosmp.listener;

import com.twicefear.diablosmp.DiabloSMP;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class SMPListener implements Listener {

    private final DiabloSMP plugin;

    public SMPListener(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (plugin.smp().isGrace() || plugin.smp().isRunning()) {
            plugin.smp().addPlayer(player);
        }
    }
}
