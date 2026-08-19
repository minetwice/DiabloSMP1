package com.twicefear.diablosmp.abilities;

import com.twicefear.diablosmp.DiabloSMP;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class InfernalAbility {

    private final DiabloSMP plugin;

    public InfernalAbility(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    public void primary(Player player) {
        // Meteor Strike - massive fire meteors from sky
        Location target = player.getTargetBlockExact(40) != null
                ? player.getTargetBlockExact(40).getLocation()
                : player.getLocation().add(player.getLocation().getDirection().multiply(20));

        player.sendMessage("§c§lInfernal Meteor incoming!");

        new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if (count >= 5) {
                    cancel();
                    return;
                }
                Location spawn = target.clone().add(
                        (Math.random() - 0.5) * 8,
                        25 + Math.random() * 10,
                        (Math.random() - 0.5) * 8
                );
                Fireball fb = player.getWorld().spawn(spawn, Fireball.class);
                fb.setDirection(target.toVector().subtract(spawn.toVector()).normalize().multiply(1.5));
                fb.setYield(3.5f);
                fb.setIsIncendiary(true);
                fb.setShooter(player);

                spawn.getWorld().spawnParticle(Particle.FLAME, spawn, 30, 0.5, 0.5, 0.5, 0.1);
                spawn.getWorld().playSound(spawn, Sound.ENTITY_BLAZE_SHOOT, 2f, 0.5f);
                count++;
            }
        }.runTaskTimer(plugin, 0L, 8L);
    }

    public void secondary(Player player) {
        // Hellfire Domain - ring of fire that burns enemies and heals owner slightly
        Location center = player.getLocation();
        player.sendMessage("§c§lHellfire Domain activated!");

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= 160) { // 8 seconds
                    cancel();
                    return;
                }

                double radius = 6;
                for (int i = 0; i < 24; i++) {
                    double angle = (Math.PI * 2 / 24) * i + ticks * 0.05;
                    Location p = center.clone().add(Math.cos(angle) * radius, 0.2, Math.sin(angle) * radius);
                    p.getWorld().spawnParticle(Particle.FLAME, p, 3, 0.1, 0.3, 0.1, 0.01);
                    p.getWorld().spawnParticle(Particle.LAVA, p, 1, 0.05, 0.05, 0.05, 0);
                }

                // Damage enemies inside
                for (Entity e : center.getWorld().getNearbyEntities(center, radius, 3, radius)) {
                    if (e instanceof Player p && p != player) {
                        p.setFireTicks(40);
                        p.damage(1.5, player);
                    }
                }

                if (ticks % 20 == 0) {
                    player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + 1));
                    player.getWorld().playSound(center, Sound.BLOCK_FIRE_AMBIENT, 1f, 1f);
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
