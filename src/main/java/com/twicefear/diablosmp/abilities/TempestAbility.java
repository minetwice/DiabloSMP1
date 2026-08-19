package com.twicefear.diablosmp.abilities;

import com.twicefear.diablosmp.DiabloSMP;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class TempestAbility {

    private final DiabloSMP plugin;

    public TempestAbility(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    public void primary(Player player) {
        // Tornado Pull - powerful vortex that pulls enemies and lifts them
        Location center = player.getLocation();
        player.sendMessage("§b§lTempest Tornado unleashed!");
        player.getWorld().playSound(center, Sound.ENTITY_PHANTOM_FLAP, 2f, 0.5f);

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= 100) { // 5 seconds
                    cancel();
                    return;
                }

                double radius = 7;
                for (int i = 0; i < 20; i++) {
                    double angle = (Math.PI * 2 / 20) * i + ticks * 0.2;
                    double y = (ticks % 20) * 0.15;
                    Location p = center.clone().add(Math.cos(angle) * radius, y, Math.sin(angle) * radius);
                    p.getWorld().spawnParticle(Particle.CLOUD, p, 2, 0.1, 0.1, 0.1, 0.02);
                    p.getWorld().spawnParticle(Particle.SWEEP_ATTACK, p, 1, 0, 0, 0, 0);
                }

                for (Entity e : center.getWorld().getNearbyEntities(center, radius, 6, radius)) {
                    if (e instanceof Player p && p != player) {
                        Vector pull = center.toVector().subtract(p.getLocation().toVector()).normalize().multiply(0.35);
                        pull.setY(0.25);
                        p.setVelocity(p.getVelocity().add(pull));
                        p.damage(0.8, player);
                    }
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public void secondary(Player player) {
        // Storm Prison - cylindrical wind prison
        Location center = player.getLocation();
        player.sendMessage("§b§lStorm Prison activated!");

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= 160) {
                    cancel();
                    return;
                }

                double radius = 5;
                for (int i = 0; i < 30; i++) {
                    double angle = (Math.PI * 2 / 30) * i;
                    for (int y = 0; y < 5; y++) {
                        Location p = center.clone().add(Math.cos(angle) * radius, y * 0.8, Math.sin(angle) * radius);
                        p.getWorld().spawnParticle(Particle.CLOUD, p, 1, 0.05, 0.05, 0.05, 0.01);
                    }
                }

                for (Entity e : center.getWorld().getNearbyEntities(center, radius + 1, 5, radius + 1)) {
                    if (e instanceof Player p && p != player) {
                        Vector away = p.getLocation().toVector().subtract(center.toVector()).normalize().multiply(0.4);
                        p.setVelocity(away);
                        p.damage(1.0, player);
                    }
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
