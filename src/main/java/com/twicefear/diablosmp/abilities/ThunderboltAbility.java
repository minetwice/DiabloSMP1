package com.twicefear.diablosmp.abilities;

import com.twicefear.diablosmp.DiabloSMP;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class ThunderboltAbility {

    private final DiabloSMP plugin;

    public ThunderboltAbility(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    public void primary(Player player) {
        // Chain Lightning
        player.sendMessage("§d§lChain Lightning!");
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.2f, 1.8f);

        List<Player> hit = new ArrayList<>();
        Location current = player.getLocation();

        new BukkitRunnable() {
            int chains = 0;
            Location last = current.clone();

            @Override
            public void run() {
                if (chains >= 6) {
                    cancel();
                    return;
                }

                Player next = null;
                double closest = 12;
                for (Entity e : last.getWorld().getNearbyEntities(last, 12, 6, 12)) {
                    if (e instanceof Player p && p != player && !hit.contains(p)) {
                        double dist = p.getLocation().distance(last);
                        if (dist < closest) {
                            closest = dist;
                            next = p;
                        }
                    }
                }

                if (next == null) {
                    cancel();
                    return;
                }

                hit.add(next);
                // Lightning visual
                Location to = next.getLocation().add(0, 1, 0);
                VectorHelper.drawLine(last.clone().add(0, 1, 0), to, Particle.ELECTRIC_SPARK);
                next.getWorld().strikeLightningEffect(next.getLocation());
                next.damage(5.0 - chains * 0.4, player);
                next.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, to, 30, 0.4, 0.8, 0.4, 0.1);

                last = next.getLocation();
                chains++;
            }
        }.runTaskTimer(plugin, 0L, 4L);
    }

    public void secondary(Player player) {
        // Thunder Domain
        Location center = player.getLocation();
        player.sendMessage("§d§lThunder Domain!");

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= 140) {
                    cancel();
                    return;
                }

                double radius = 7;
                if (ticks % 15 == 0) {
                    for (int i = 0; i < 3; i++) {
                        double angle = Math.random() * Math.PI * 2;
                        Location strike = center.clone().add(Math.cos(angle) * (Math.random() * radius), 0, Math.sin(angle) * (Math.random() * radius));
                        strike.getWorld().strikeLightningEffect(strike);
                        strike.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, strike, 20, 0.5, 1, 0.5, 0.1);

                        for (Entity e : strike.getWorld().getNearbyEntities(strike, 2.5, 3, 2.5)) {
                            if (e instanceof Player p && p != player) {
                                p.damage(4.0, player);
                            }
                        }
                    }
                }

                // Border particles
                for (int i = 0; i < 12; i++) {
                    double angle = (Math.PI * 2 / 12) * i + ticks * 0.05;
                    Location p = center.clone().add(Math.cos(angle) * radius, 1, Math.sin(angle) * radius);
                    p.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, p, 2, 0.1, 0.3, 0.1, 0.02);
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    // Simple helper
    private static class VectorHelper {
        static void drawLine(Location from, Location to, Particle particle) {
            Vector dir = to.toVector().subtract(from.toVector());
            double length = dir.length();
            dir.normalize();
            for (double d = 0; d < length; d += 0.4) {
                Location point = from.clone().add(dir.clone().multiply(d));
                point.getWorld().spawnParticle(particle, point, 1, 0, 0, 0, 0);
            }
        }
    }
}
