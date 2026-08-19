package com.twicefear.diablosmp.abilities;

import com.twicefear.diablosmp.DiabloSMP;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CelestialAbility {

    private final DiabloSMP plugin;

    public CelestialAbility(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    public void primary(Player player) {
        // Starfall Barrage
        Location target = player.getTargetBlockExact(30) != null
                ? player.getTargetBlockExact(30).getLocation()
                : player.getLocation().add(player.getLocation().getDirection().multiply(18));

        player.sendMessage("§f§lStarfall!");
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.5f, 0.8f);

        new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if (count >= 8) {
                    cancel();
                    return;
                }

                Location spawn = target.clone().add(
                        (Math.random() - 0.5) * 10,
                        18 + Math.random() * 8,
                        (Math.random() - 0.5) * 10
                );

                new BukkitRunnable() {
                    Location current = spawn.clone();
                    @Override
                    public void run() {
                        if (current.getY() <= target.getY() + 1) {
                            current.getWorld().spawnParticle(Particle.FIREWORK, current, 30, 0.8, 0.4, 0.8, 0.1);
                            current.getWorld().spawnParticle(Particle.END_ROD, current, 15, 0.5, 0.3, 0.5, 0.05);
                            current.getWorld().playSound(current, Sound.ENTITY_GENERIC_EXPLODE, 1.2f, 1.4f);

                            for (Entity e : current.getWorld().getNearbyEntities(current, 3.5, 3, 3.5)) {
                                if (e instanceof Player p && p != player) {
                                    p.damage(5.5, player);
                                }
                            }
                            cancel();
                            return;
                        }
                        current.add(0, -1.2, 0);
                        current.getWorld().spawnParticle(Particle.END_ROD, current, 3, 0.1, 0.1, 0.1, 0.01);
                        current.getWorld().spawnParticle(Particle.FIREWORK, current, 1, 0.05, 0.05, 0.05, 0);
                    }
                }.runTaskTimer(plugin, 0L, 1L);

                count++;
            }
        }.runTaskTimer(plugin, 0L, 6L);
    }

    public void secondary(Player player) {
        // Cosmic Prison
        Location center = player.getLocation();
        player.sendMessage("§f§lCosmic Prison!");

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= 160) {
                    cancel();
                    return;
                }

                double radius = 6.5;
                for (int i = 0; i < 20; i++) {
                    double angle = (Math.PI * 2 / 20) * i + ticks * 0.05;
                    double y = Math.sin(ticks * 0.1 + i) * 1.5;
                    Location p = center.clone().add(Math.cos(angle) * radius, 1 + y, Math.sin(angle) * radius);
                    p.getWorld().spawnParticle(Particle.END_ROD, p, 2, 0.05, 0.1, 0.05, 0.01);
                    p.getWorld().spawnParticle(Particle.FIREWORK, p, 1, 0.05, 0.05, 0.05, 0);
                }

                // Stars inside
                if (ticks % 5 == 0) {
                    center.getWorld().spawnParticle(Particle.END_ROD, center.clone().add(0, 2, 0), 8, 2, 1.5, 2, 0.02);
                }

                for (Entity e : center.getWorld().getNearbyEntities(center, radius, 5, radius)) {
                    if (e instanceof Player p && p != player) {
                        if (ticks % 12 == 0) p.damage(2.2, player);
                        p.setVelocity(p.getVelocity().multiply(0.7)); // slow movement
                    }
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
