package com.twicefear.diablosmp.abilities;

import com.twicefear.diablosmp.DiabloSMP;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class DragonheartAbility {

    private final DiabloSMP plugin;

    public DragonheartAbility(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    public void primary(Player player) {
        // Dragon Breath Cone
        player.sendMessage("§2§lDragon Breath!");
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.5f, 0.8f);

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= 35 || !player.isOnline()) {
                    cancel();
                    return;
                }

                Location eye = player.getEyeLocation();
                Vector dir = eye.getDirection().normalize();

                for (int i = 1; i <= 12; i++) {
                    Location point = eye.clone().add(dir.clone().multiply(i * 0.9));
                    double spread = i * 0.15;
                    point.getWorld().spawnParticle(Particle.DRAGON_BREATH, point, 8, spread, 0.2, spread, 0.02);
                    point.getWorld().spawnParticle(Particle.FLAME, point, 3, spread * 0.5, 0.1, spread * 0.5, 0.01);

                    for (Entity e : point.getWorld().getNearbyEntities(point, 1.4 + spread, 1.4, 1.4 + spread)) {
                        if (e instanceof Player p && p != player) {
                            p.damage(2.8, player);
                            p.setFireTicks(60);
                        }
                    }
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public void secondary(Player player) {
        // Dragon Roost Domain
        Location center = player.getLocation();
        player.sendMessage("§2§lDragon Roost!");

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= 180) {
                    cancel();
                    return;
                }

                double radius = 8;
                for (int i = 0; i < 16; i++) {
                    double angle = (Math.PI * 2 / 16) * i + ticks * 0.04;
                    Location p = center.clone().add(Math.cos(angle) * radius, 0.5, Math.sin(angle) * radius);
                    p.getWorld().spawnParticle(Particle.DRAGON_BREATH, p, 2, 0.2, 0.4, 0.2, 0.01);
                    p.getWorld().spawnParticle(Particle.FLAME, p, 1, 0.1, 0.2, 0.1, 0);
                }

                // Center column
                center.getWorld().spawnParticle(Particle.DRAGON_BREATH, center.clone().add(0, 2, 0), 5, 0.3, 1.5, 0.3, 0.02);

                for (Entity e : center.getWorld().getNearbyEntities(center, radius, 5, radius)) {
                    if (e instanceof Player p && p != player) {
                        p.setFireTicks(40);
                        if (ticks % 15 == 0) p.damage(3.0, player);
                    }
                }

                if (ticks % 25 == 0) {
                    player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + 2));
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
