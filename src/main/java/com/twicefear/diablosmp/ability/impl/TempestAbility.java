package com.twicefear.diablosmp.ability.impl;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.stone.StoneType;
import com.twicefear.diablosmp.util.Particles;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Tempest stone.
 * Primary: Cyclone that pulls and launches entities. Secondary: Thunderstorm dome.
 */
public class TempestAbility extends AbstractAbility {

    public TempestAbility(DiabloSMP plugin) {
        super(plugin, StoneType.TEMPEST);
    }

    @Override
    public void onPrimary(Player player, PlayerInteractEvent event) {
        if (primaryOnCooldown(player)) return;
        startPrimaryCooldown(player);

        Location center = player.getLocation().add(player.getLocation().getDirection().setY(0).normalize().multiply(5));
        World world = center.getWorld();

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks > 80) { cancel(); return; }
                double angle = ticks * 0.5;
                for (int i = 0; i < 20; i++) {
                    double a = angle + (Math.PI * 2 * i / 20);
                    double r = 1 + (i % 5) * 0.6;
                    double h = i * 0.4;
                    Location loc = center.clone().add(Math.cos(a) * r, h, Math.sin(a) * r);
                    world.spawnParticle(Particle.CLOUD, loc, 0, 0, 0, 0, null);
                    world.spawnParticle(Particle.SWEEP_ATTACK, loc, 0, 0, 0, 0, null);
                }
                Particles.spiral(center, 2, 4, 3, Particle.CLOUD, color(), 30);
                for (Entity e : world.getNearbyEntities(center, 5, 5, 5)) {
                    if (e instanceof LivingEntity le && !e.equals(player)) {
                        Vector to = center.toVector().subtract(le.getLocation().toVector()).normalize().multiply(0.2);
                        le.setVelocity(le.getVelocity().add(to));
                        if (ticks == 60) {
                            le.setVelocity(new Vector(0, 1.5, 0));
                            le.damage(6, player);
                        }
                    }
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
        world.setStorm(true);

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks > 120) { world.setStorm(false); cancel(); return; }
                Particles.sphere(center, 8, Particle.ELECTRIC_SPARK, color(), 20);
                if (ticks % 15 == 0) {
                    double a = Math.random() * Math.PI * 2;
                    double r = Math.random() * 7;
                    Location strike = center.clone().add(Math.cos(a) * r, 0, Math.sin(a) * r);
                    world.strikeLightning(strike);
                    Particles.burst(strike, Particle.ELECTRIC_SPARK, color(), 30, 0.3);
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
