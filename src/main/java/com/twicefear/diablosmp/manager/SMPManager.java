package com.twicefear.diablosmp.manager;

import com.twicefear.diablosmp.DiabloSMP;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SMPManager {

    public enum State { IDLE, GRACE, RUNNING }

    private final DiabloSMP plugin;
    private State state = State.IDLE;
    private int graceSecondsTotal;
    private int graceSecondsRemaining;
    private BossBar bossBar;
    private BukkitRunnable timerTask;

    public SMPManager(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    public State getState() { return state; }
    public boolean isRunning() { return state == State.RUNNING; }
    public boolean isGrace() { return state == State.GRACE; }
    public boolean isIdle() { return state == State.IDLE; }

    public void start(int minutes, int seconds) {
        if (state != State.IDLE) return;
        state = State.GRACE;
        graceSecondsTotal = minutes * 60 + seconds;
        graceSecondsRemaining = graceSecondsTotal;

        for (World world : Bukkit.getWorlds()) {
            double startSize = plugin.config().borderStart();
            world.getWorldBorder().setSize(startSize);
            world.getWorldBorder().setDamageAmount(0);
            world.getWorldBorder().setDamageBuffer(5);
        }

        String name = ChatColor.translateAlternateColorCodes('&',
                "&c&l" + plugin.config().smpName() + " &7\u00bb &eGrace Period");
        bossBar = BossBar.bossBar(
                net.kyori.adventure.text.Component.text(name),
                1.0f, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
        for (Player p : Bukkit.getOnlinePlayers()) bossBar.addPlayer(p);

        timerTask = new BukkitRunnable() {
            @Override
            public void run() { tick(); }
        };
        timerTask.runTaskTimer(plugin, 20L, 20L);
        Bukkit.broadcastMessage(plugin.messages().prefixed("smp-started"));
    }

    private void tick() {
        if (graceSecondsRemaining <= 0) { endGrace(); return; }
        graceSecondsRemaining--;
        float progress = graceSecondsTotal > 0 ? (float) graceSecondsRemaining / graceSecondsTotal : 0;
        progress = Math.max(0f, Math.min(1f, progress));
        bossBar.progress(progress);
        int mins = graceSecondsRemaining / 60;
        int secs = graceSecondsRemaining % 60;
        String timeStr = String.format("%d:%02d", mins, secs);
        String name = ChatColor.translateAlternateColorCodes('&',
                "&c&l" + plugin.config().smpName() + " &7\u00bb &e" + timeStr);
        bossBar.name(net.kyori.adventure.text.Component.text(name));

        if (graceSecondsRemaining <= 10 && graceSecondsRemaining > 0) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendTitle("\u00a7c\u00a7l" + graceSecondsRemaining, "", 5, 15, 5);
                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            }
        }
    }

    private void endGrace() {
        state = State.RUNNING;
        for (World world : Bukkit.getWorlds()) {
            if (plugin.config().borderInfinite()) {
                world.getWorldBorder().setSize(60000000);
            } else {
                world.getWorldBorder().setSize(plugin.config().borderEnd(), plugin.config().expandSeconds());
            }
        }
        if (bossBar != null) { bossBar.removeAll(); bossBar = null; }
        if (timerTask != null) { timerTask.cancel(); timerTask = null; }
        showAnnouncements();
    }

    private void showAnnouncements() {
        var announcements = plugin.config().announcements();
        int[] index = {0};
        new BukkitRunnable() {
            @Override
            public void run() {
                if (index[0] >= announcements.size()) {
                    Bukkit.broadcastMessage(plugin.messages().prefixed("smp-running"));
                    cancel();
                    return;
                }
                String line = ChatColor.translateAlternateColorCodes('&', announcements.get(index[0]));
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendTitle(line, "", 5, 40, 10);
                    p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);
                }
                index[0]++;
            }
        }.runTaskTimer(plugin, 0L, 40L);
    }

    public void stop() {
        state = State.IDLE;
        if (bossBar != null) { bossBar.removeAll(); bossBar = null; }
        if (timerTask != null) { timerTask.cancel(); timerTask = null; }
        Bukkit.broadcastMessage(plugin.messages().prefixed("smp-stopped"));
    }

    public void addPlayer(Player p) { if (bossBar != null) bossBar.addPlayer(p); }

    public void shutdown() {
        if (bossBar != null) { bossBar.removeAll(); bossBar = null; }
        if (timerTask != null) { timerTask.cancel(); timerTask = null; }
    }

    public void changeName(String name) {
        plugin.config().setSmpName(name);
        if (bossBar != null && state == State.GRACE) {
            String barName = ChatColor.translateAlternateColorCodes('&',
                    "&c&l" + name + " &7\u00bb &eGrace Period");
            bossBar.name(net.kyori.adventure.text.Component.text(barName));
        }
    }

    public int getGraceRemaining() { return graceSecondsRemaining; }
}
