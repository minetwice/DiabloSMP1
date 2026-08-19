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
 * Arcane stone.
 *
 * Primary: Fires a homing arcane missile that tracks the nearest enemy
 * and explodes into a rune pattern on impact.
 *
 * Secondary: Arcane nova - a massive rune expands outward, silencing and
 * damaging all enemies caught in the wave.
 */
public class ArcaneAbility extends AbstractAbility {

    public ArcaneAbility(DiabloSMP plugin) {
        super(plugin, StoneType.ARCANE);
    }

    @Override
    public void onPrimary(Player player, PlayerInteractEvent event) {
        if (primaryOnCooldown(player)) return;
        startPrimaryCooldown(player);

        Location start = player.getEyeLocation();
        World world = start.getWorld();
        Location[] cur = {start.clone()};

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks > 60) { explode(cur[0], player); cancel(); return; }
                LivingEntity target = null;
                double best = 25 * 25;
                for (Entity e : world.getNearbyEntities(cur[0], 15, 15, 15)) {
                    if (e instanceof LivingEntity le && !e.equals(player)) {
                        double d = le.getLocation().distanceSquared(cur[0]);
                        if (d < best) { best = d; target = le; }
                    }
                }
                Vector dir;
                if (target != null) {
                    dir = target.getEyeLocation().toVector().subtract(cur[0].toVector()).normalize().multiply(1.2);
                } else {
                    dir = player.getLocation().getDirection().multiply(1.2);
                }
                cur[0].add(dir);
                Particles.orbit(cur[0], 0.6, 0, ticks * 0.5, Particle.DUST, color(), 6);
                world.spawnParticle(Particle.ENCHANT, cur[0], 3, 0.1, 0.1, 0.1, 0.1);
                world.spawnParticle(Particle.WITCH, cur[0], 2, 0.1, 0.1, 0.1, 0.01);
                for (Entity e : world.getNearbyEntities(cur[0], 1.5, 1.5, 1.5)) {
                    if (e instanceof LivingEntity le && !e.equals(player)) {
                        explode(cur[0], player);
                        cancel();
                        return;
                    }
                }
                if (cur[0].getBlock().getType().isSolid()) { explode(cur[0], player); cancel(); return; }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void explode(Location loc, Player player) {
        Particles.burst(loc, Particle.DUST, color(), 50, 0.4);
        Particles.ring(loc, 3, Particle.ENCHANT, color(), 30);
        Particles.ring(loc, 2, Particle.DUST, color(), 20);
        loc.getWorld().playSound(loc, Sound.BLOCK_BEACON_POWER_SELECT, 1.5f, 1.5f);
        for (Entity e : loc.getWorld().getNearbyEntities(loc, 4, 4, 4)) {
            if (e instanceof LivingEntity le && !e.equals(player)) {
                le.damage(8, player);
                le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 2));
            }
        }
    }

    @Override
    public void onSecondary(Player player, PlayerInteractEvent event) {
        if (secondaryOnCooldown(player)) return;
        startSecondaryCooldown(player);

        Location center = player.getLocation();
        World world = center.getWorld();

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks > 100) { cancel(); return; }
                double r = ticks * 0.3;
                Particles.ring(center, r, Particle.DUST, color(), 50);
                Particles.ring(center, r * 0.95, Particle.ENCHANT, color(), 30);
                for (int i = 0; i < 6; i++) {
                    double a = ticks * 0.1 + (Math.PI * 2 * i / 6);
                    Location rune = center.clone().add(Math.cos(a) * r, 1, Math.sin(a) * r);
                    world.spawnParticle(Particle.WITCH, rune, 3, 0, 0, 0, 0.02);
                }
                if (ticks % 10 == 0) {
                    for (Entity e : world.getNearbyEntities(center, r + 1, 3, r + 1)) {
                        if (e instanceof LivingEntity le && !e.equals(player)) {
                            double dist = center.distance(le.getLocation());
                            if (dist > r - 2 && dist < r + 2) {
                                le.damage(5, player);
                                le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 2));
                                le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 1));
                                Particles.burst(le.getLocation(), Particle.DUST, color(), 15, 0.2);
                            }
                        }
                    }
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
