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
 * Lightning stone.
 * Primary: Chain lightning (5 jumps). Secondary: Lightning storm waves.
 */
public class LightningAbility extends AbstractAbility {

    public LightningAbility(DiabloSMP plugin) {
        super(plugin, StoneType.LIGHTNING);
    }

    @Override
    public void onPrimary(Player player, PlayerInteractEvent event) {
        if (primaryOnCooldown(player)) return;
        startPrimaryCooldown(player);

        Location start = player.getEyeLocation();
        Vector dir = start.getDirection().multiply(2);
        World world = start.getWorld();
        Location[] cur = {start.clone()};
        int[] hits = {0};

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks > 30 || hits[0] >= 5) { cancel(); return; }
                cur[0].add(dir);
                world.spawnParticle(Particle.ELECTRIC_SPARK, cur[0], 3, 0.1, 0.1, 0.1, 0.1);
                for (Entity e : world.getNearbyEntities(cur[0], 2, 2, 2)) {
                    if (e instanceof LivingEntity le && !e.equals(player) && !le.hasMetadata("diablo_chained")) {
                        le.setMetadata("diablo_chained", new org.bukkit.metadata.FixedMetadataValue(plugin, true));
                        le.damage(6, player);
                        world.strikeLightning(le.getLocation());
                        Particles.burst(le.getLocation(), Particle.ELECTRIC_SPARK, color(), 20, 0.2);
                        hits[0]++;
                        for (Entity next : world.getNearbyEntities(le.getLocation(), 8, 8, 8)) {
                            if (next instanceof LivingEntity nle && !next.equals(player) && !next.equals(e)
                                    && !nle.hasMetadata("diablo_chained")) {
                                Particles.line(le.getLocation(), nle.getLocation(), Particle.ELECTRIC_SPARK, color(), 10);
                                cur[0] = nle.getLocation().clone();
                                break;
                            }
                        }
                    }
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Entity e : world.getEntities()) {
                if (e instanceof LivingEntity) e.removeMetadata("diablo_chained", plugin);
            }
        }, 40L);
    }

    @Override
    public void onSecondary(Player player, PlayerInteractEvent event) {
        if (secondaryOnCooldown(player)) return;
        startSecondaryCooldown(player);

        Location center = player.getLocation();
        World world = center.getWorld();

        new BukkitRunnable() {
            int ticks = 0;
            double radius = 2;
            @Override
            public void run() {
                if (ticks > 100) { cancel(); return; }
                radius += 0.5;
                if (radius > 15) radius = 2;
                if (ticks % 8 == 0) {
                    for (int i = 0; i < 3; i++) {
                        double a = Math.random() * Math.PI * 2;
                        Location strike = center.clone().add(Math.cos(a) * radius, 0, Math.sin(a) * radius);
                        world.strikeLightning(strike);
                    }
                }
                Particles.ring(center, radius, Particle.ELECTRIC_SPARK, color(), 30);
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
