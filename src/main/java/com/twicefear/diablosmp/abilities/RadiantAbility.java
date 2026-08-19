package com.twicefear.diablosmp.abilities;

import com.twicefear.diablosmp.DiabloSMP;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class RadiantAbility {

    private final DiabloSMP plugin;

    public RadiantAbility(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    public void primary(Player player) {
        // Holy Beam - powerful continuous beam
        player.sendMessage("§e§lHoly Beam!");
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.5f, 1.5f);

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= 40 || !player.isOnline()) {
                    cancel();
                    return;
                }

                Location eye = player.getEyeLocation();
                Vector dir = eye.getDirection().normalize();

                for (int i = 1; i <= 25; i++) {
                    Location point = eye.clone().add(dir.clone().multiply(i * 0.7));
                    point.getWorld().spawnParticle(Particle.END_ROD, point, 2, 0.05, 0.05, 0.05, 0.01);
                    point.getWorld().spawnParticle(Particle.FIREWORK, point, 1, 0.02, 0.02, 0.02, 0);

                    for (Entity e : point.getWorld().getNearbyEntities(point, 1.0, 1.0, 1.0)) {
                        if (e instanceof Player p && p != player) {
                            p.damage(2.2, player);
                            p.setFireTicks(0);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 40, 0));
                        }
                    }
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public void secondary(Player player) {
        // Sanctuary Domain - healing + protection zone
        Location center = player.getLocation();
        player.sendMessage("§e§lSanctuary Domain!");

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= 160) {
                    cancel();
                    return;
                }

                double radius = 6;
                for (int i = 0; i < 20; i++) {
                    double angle = (Math.PI * 2 / 20) * i + ticks * 0.04;
                    Location p = center.clone().add(Math.cos(angle) * radius, 0.3, Math.sin(angle) * radius);
                    p.getWorld().spawnParticle(Particle.END_ROD, p, 2, 0.1, 0.2, 0.1, 0.01);
                    p.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, p, 1, 0.05, 0.1, 0.05, 0);
                }

                for (Entity e : center.getWorld().getNearbyEntities(center, radius, 3, radius)) {
                    if (e instanceof Player p) {
                        if (p == player || p.getUniqueId().equals(player.getUniqueId())) {
                            if (ticks % 15 == 0) {
                                p.setHealth(Math.min(p.getMaxHealth(), p.getHealth() + 1.5));
                                p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 30, 0));
                            }
                        } else {
                            p.damage(1.0, player);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 30, 0));
                        }
                    }
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
