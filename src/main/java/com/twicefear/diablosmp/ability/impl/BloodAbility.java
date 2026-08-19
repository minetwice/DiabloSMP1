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
 * Blood stone.
 *
 * Primary: Fires a blood projectile that lifesteals.
 * Secondary: Blood ritual - creates a crimson field that drains enemies.
 */
public class BloodAbility extends AbstractAbility {

    public BloodAbility(DiabloSMP plugin) {
        super(plugin, StoneType.BLOOD);
    }

    @Override
    public void onPrimary(Player player, PlayerInteractEvent event) {
        if (primaryOnCooldown(player)) return;
        startPrimaryCooldown(player);

        Location start = player.getEyeLocation();
        Vector dir = start.getDirection().multiply(1.5);
        World world = start.getWorld();
        Location[] cur = {start.clone()};
        boolean[] hit = {false};

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks > 40 || hit[0]) { cancel(); return; }
                cur[0].add(dir);
                world.spawnParticle(Particle.DUST, cur[0], 5, 0.1, 0.1, 0.1,
                        new Particle.DustOptions(color(), 1.5f));
                Particles.line(cur[0].clone().subtract(dir), cur[0], Particle.DUST, color(), 3);
                for (Entity e : world.getNearbyEntities(cur[0], 1.5, 1.5, 1.5)) {
                    if (e instanceof LivingEntity le && !e.equals(player)) {
                        double dmg = 7;
                        le.damage(dmg, player);
                        player.heal(dmg * 0.5);
                        hit[0] = true;
                        Particles.burst(le.getLocation(), Particle.DUST, color(), 25, 0.3);
                        Particles.line(le.getLocation(), player.getEyeLocation(), Particle.DUST, color(), 8);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 1));
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

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks > 150) { cancel(); return; }
                Particles.ring(center, 6, Particle.DUST, color(), 40);
                Particles.sphere(center.clone().add(0, 1, 0), 5, Particle.DUST, color(), 10);
                if (ticks % 10 == 0) {
                    for (Entity e : world.getNearbyEntities(center, 6, 4, 6)) {
                        if (e instanceof LivingEntity le && !e.equals(player)) {
                            le.damage(3, player);
                            le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 30, 1));
                            Particles.line(le.getLocation().add(0, 1, 0), player.getEyeLocation(),
                                    Particle.DUST, color(), 5);
                            player.heal(3);
                        }
                    }
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
