package com.twicefear.diablosmp.ability.impl;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.stone.StoneType;
import com.twicefear.diablosmp.util.Particles;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Inferno stone.
 * Primary: Spiraling fireball. Secondary: Fire tornado + eruption.
 */
public class InfernoAbility extends AbstractAbility {

    public InfernoAbility(DiabloSMP plugin) {
        super(plugin, StoneType.INFERNO);
    }

    @Override
    public void onPrimary(Player player, PlayerInteractEvent event) {
        if (primaryOnCooldown(player)) return;
        startPrimaryCooldown(player);

        Location start = player.getEyeLocation();
        World world = start.getWorld();
        Vector dir = start.getDirection().multiply(1.2);
        Location[] cur = {start.clone()};

        new BukkitRunnable() {
            int ticks = 0;
            double angle = 0;
            @Override
            public void run() {
                if (ticks > 60) { cancel(); return; }
                angle += 0.6;
                Location next = cur[0].add(dir);
                Particles.orbit(next, 0.8, 0, angle, Particle.FLAME, null, 8);
                Particles.orbit(next, 0.5, 0.2, -angle, Particle.LAVA, null, 4);
                world.spawnParticle(Particle.SMALL_FLAME, next, 3, 0.2, 0.2, 0.2, 0.02);
                cur[0] = next;
                for (Entity e : world.getNearbyEntities(next, 2, 2, 2)) {
                    if (e instanceof LivingEntity le && !e.equals(player)) {
                        le.setFireTicks(60);
                        le.damage(4, player);
                    }
                }
                if (next.getBlock().getType().isSolid()) {
                    Particles.burst(next, Particle.FLAME, color(), 40, 0.3);
                    Particles.ring(next, 3, Particle.LAVA, color(), 24);
                    for (Entity e : world.getNearbyEntities(next, 3, 3, 3)) {
                        if (e instanceof LivingEntity le && !e.equals(player)) {
                            le.setFireTicks(120);
                            le.damage(6, player);
                        }
                    }
                    cancel();
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    @Override
    public void onSecondary(Player player, PlayerInteractEvent event) {
        if (secondaryOnCooldown(player)) return;
        startSecondaryCooldown(player);

        Location center = player.getLocation();
        World world = center.getWorld();

        new BukkitRunnable() {
            int ticks = 0;
            double radius = 1;
            double height = 0;
            @Override
            public void run() {
                if (ticks > 120) {
                    Particles.burst(center, Particle.FLAME, color(), 80, 0.8);
                    Particles.burst(center, Particle.LAVA, color(), 40, 0.4);
                    world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 2f, 0.8f);
                    for (Entity e : world.getNearbyEntities(center, 8, 8, 8)) {
                        if (e instanceof LivingEntity le && !e.equals(player)) {
                            le.setFireTicks(200);
                            le.damage(12, player);
                            le.setVelocity(le.getLocation().toVector().subtract(center.toVector()).normalize().multiply(1.5).setY(0.8));
                        }
                    }
                    cancel();
                    return;
                }
                radius = Math.min(6, radius + 0.08);
                height = Math.min(8, height + 0.15);
                for (int i = 0; i < 3; i++) {
                    double a = ticks * 0.4 + (i * Math.PI * 2 / 3);
                    double r = radius * (0.4 + 0.6 * Math.random());
                    double h = height * Math.random();
                    Location loc = center.clone().add(Math.cos(a) * r, h, Math.sin(a) * r);
                    world.spawnParticle(Particle.FLAME, loc, 0, 0, 0, 0, null);
                    world.spawnParticle(Particle.LARGE_SMOKE, loc, 0, 0, 0, 0, null);
                }
                if (ticks % 5 == 0) {
                    for (Entity e : world.getNearbyEntities(center, 7, 7, 7)) {
                        if (e instanceof LivingEntity le && !e.equals(player)) {
                            Vector pull = center.toVector().subtract(le.getLocation().toVector()).normalize().multiply(0.15);
                            le.setVelocity(le.getVelocity().add(pull));
                            le.setFireTicks(40);
                        }
                    }
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
