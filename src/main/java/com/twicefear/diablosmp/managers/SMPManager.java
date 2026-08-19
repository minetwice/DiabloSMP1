package com.twicefear.diablosmp.managers;

import com.twicefear.diablosmp.DiabloSMP;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class SMPManager {

    private final DiabloSMP plugin;
    private boolean running = false;
    private BossBar graceBossBar;
    private BukkitTask graceTask;
    private int remainingSeconds;

    public SMPManager(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    public boolean isRunning() {
        return running;
    }

    public void startSMP(int graceSeconds) {
        if (running) return;

        running = true;
        plugin.getConfigManager().setSmpStarted(true);
        remainingSeconds = graceSeconds;

        // Set world border small
        for (World world : Bukkit.getWorlds()) {
            WorldBorder border = world.getWorldBorder();
            border.setCenter(0, 0);
            border.setSize(plugin.getConfigManager().getWorldBorderStartSize());
        }

        // Create BossBar
        String name = plugin.getConfigManager().getSmpName();
        graceBossBar = BossBar.bossBar(
                Component.text(name + " - Grace Period: " + formatTime(remainingSeconds))
                        .color(NamedTextColor.GOLD),
                1.0f,
                BossBar.Color.RED,
                BossBar.Overlay.PROGRESS
        );

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.showBossBar(graceBossBar);
        }

        // Start countdown
        graceTask = new BukkitRunnable() {
            @Override
            public void run() {
                remainingSeconds--;
                if (remainingSeconds <= 0) {
                    endGracePeriod();
                    cancel();
                    return;
                }

                float progress = (float) remainingSeconds / graceSeconds;
                graceBossBar.progress(Math.max(0, progress));
                graceBossBar.name(Component.text(name + " - Grace Period: " + formatTime(remainingSeconds))
                        .color(NamedTextColor.GOLD));

                // Final 10 second countdown
                if (remainingSeconds <= 10) {
                    Bukkit.broadcast(Component.text("§c§l" + remainingSeconds + "..."));
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);

        Bukkit.broadcast(Component.text("§a§l" + name + " has started! Grace period: " + formatTime(graceSeconds)));
    }

    private void endGracePeriod() {
        String name = plugin.getConfigManager().getSmpName();

        // Hide bossbar
        if (graceBossBar != null) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.hideBossBar(graceBossBar);
            }
        }

        // Show popups
        for (String line : plugin.getConfigManager().getPopupLines()) {
            Bukkit.broadcast(Component.text(line.replace('&', '§')));
        }

        // Expand world border
        int finalSize = plugin.getConfigManager().getWorldBorderFinalSize();
        int duration = plugin.getConfigManager().getWorldBorderExpandDuration();

        for (World world : Bukkit.getWorlds()) {
            WorldBorder border = world.getWorldBorder();
            if (finalSize <= 0) {
                border.setSize(60000000); // practically infinite
            } else {
                border.setSize(finalSize, duration);
            }
        }

        Bukkit.broadcast(Component.text("§c§lGrace period ended! World border expanding..."));
    }

    public void stopSMP() {
        running = false;
        plugin.getConfigManager().setSmpStarted(false);
        if (graceTask != null) graceTask.cancel();
        if (graceBossBar != null) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.hideBossBar(graceBossBar);
            }
        }
    }

    public void shutdown() {
        stopSMP();
    }

    private String formatTime(int seconds) {
        int min = seconds / 60;
        int sec = seconds % 60;
        return String.format("%02d:%02d", min, sec);
    }

    public void showBossBarTo(Player player) {
        if (graceBossBar != null && running) {
            player.showBossBar(graceBossBar);
        }
    }
}
