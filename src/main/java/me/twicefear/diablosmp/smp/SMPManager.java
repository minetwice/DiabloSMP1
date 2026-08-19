package me.twicefear.diablosmp.smp;

import me.twicefear.diablosmp.DiabloSMP;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class SMPManager {

    private final DiabloSMP plugin;
    private BossBar bossBar;
    private BukkitTask graceTask;
    private int graceSecondsRemaining;
    private int totalGraceSeconds;

    public SMPManager(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    public boolean isStarted() {
        return plugin.getConfig().getBoolean("smp.started", false);
    }

    public String getSmpName() {
        String name = plugin.getConfig().getString("smp.name", "DiabloSmp");
        return ChatColor.translateAlternateColorCodes('&', name);
    }

    public void startSmp(int minutes, int seconds) {
        int totalSecs = (minutes * 60) + seconds;
        if (totalSecs <= 0) totalSecs = 1;

        this.totalGraceSeconds = totalSecs;
        this.graceSecondsRemaining = totalSecs;

        plugin.getConfig().set("smp.started", true);
        plugin.getConfig().set("smp.grace_period_seconds", totalSecs);
        plugin.saveConfig();

        // Setup world border during grace period
        World world = Bukkit.getWorlds().get(0);
        if (world != null) {
            double graceBorderSize = plugin.getConfig().getDouble("smp.world_border.grace_size", 20.0);
            world.getWorldBorder().setCenter(0, 0);
            world.getWorldBorder().setSize(graceBorderSize);
        }

        // Setup BossBar
        if (bossBar != null) {
            bossBar.removeAll();
        }

        bossBar = Bukkit.createBossBar(
                getBossBarTitle(graceSecondsRemaining),
                BarColor.RED,
                BarStyle.SOLID
        );

        for (Player p : Bukkit.getOnlinePlayers()) {
            bossBar.addPlayer(p);
        }

        // Broadcast start
        Bukkit.broadcastMessage(ChatColor.GOLD + "========================================");
        Bukkit.broadcastMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + getSmpName() + ChatColor.RED + " HAS STARTED!");
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Grace Period duration: " + ChatColor.WHITE + formatTime(graceSecondsRemaining));
        Bukkit.broadcastMessage(ChatColor.GOLD + "========================================");

        if (graceTask != null) graceTask.cancel();

        graceTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (bossBar != null) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (!bossBar.getPlayers().contains(p)) {
                            bossBar.addPlayer(p);
                        }
                    }
                }

                if (graceSecondsRemaining <= 0) {
                    endGracePeriod();
                    cancel();
                    return;
                }

                bossBar.setTitle(getBossBarTitle(graceSecondsRemaining));
                bossBar.setProgress((double) graceSecondsRemaining / totalGraceSeconds);

                if (graceSecondsRemaining <= 10) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f, 1.5f);
                        p.sendTitle(ChatColor.RED + "" + graceSecondsRemaining, ChatColor.YELLOW + "Grace period ending...", 0, 20, 5);
                    }
                }

                graceSecondsRemaining--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void endGracePeriod() {
        if (bossBar != null) {
            bossBar.removeAll();
            bossBar = null;
        }

        // Set active world border size
        World world = Bukkit.getWorlds().get(0);
        if (world != null) {
            Object activeSizeObj = plugin.getConfig().get("smp.world_border.active_size", 20000.0);
            if (activeSizeObj.toString().equalsIgnoreCase("infinite") || activeSizeObj.toString().equalsIgnoreCase("-1")) {
                world.getWorldBorder().setSize(30000000.0, 10);
            } else {
                double activeSize = Double.parseDouble(activeSizeObj.toString());
                world.getWorldBorder().setSize(activeSize, 10);
            }
        }

        // Custom multi-line popups & announcements
        List<String> announcements = plugin.getConfig().getStringList("smp.announcements");
        new BukkitRunnable() {
            int index = 0;

            @Override
            public void run() {
                if (index >= announcements.size()) {
                    cancel();
                    return;
                }

                String line = ChatColor.translateAlternateColorCodes('&', announcements.get(index));
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendTitle(getSmpName(), line, 10, 50, 10);
                    p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
                }

                index++;
            }
        }.runTaskTimer(plugin, 0L, 60L);
    }

    private String getBossBarTitle(int seconds) {
        return ChatColor.DARK_RED + "" + ChatColor.BOLD + getSmpName() +
                ChatColor.GRAY + " - Grace Period: " + ChatColor.YELLOW + formatTime(seconds);
    }

    private String formatTime(int totalSecs) {
        int m = totalSecs / 60;
        int s = totalSecs % 60;
        return String.format("%02d:%02d", m, s);
    }

    public BossBar getBossBar() {
        return bossBar;
    }
}
