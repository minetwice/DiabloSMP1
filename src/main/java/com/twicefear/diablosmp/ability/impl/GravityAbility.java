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
 * Gravity stone.
 * Primary: Gravity well at cursor. Secondary: Gravity inversion + crash.
 */
public class GravityAbility extends AbstractAbility {

    public GravityAbility(DiabloSMP plugin) {
        super(plugin, StoneType.GRAVITY);
    }

    @Override
    public void onPrimary(Player player, PlayerInteractEvent event) {
        if (primaryOnCooldown(player)) return;
        startPrimaryCooldown(player);

        Location center = player.getTargetBlockExact(20) != null
                ? player.getTargetBlockExact(20).getLocation()
                : player.getLocation().add(player.getLocation().getDirection().multiply(10));
        World world = center.getWorld();

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks > 60) { cancel(); return; }
                for (int i = 0; i < 20; i++) {
                    double a = ticks * 0.3 + (Math.PI * 2 * i / 20);
                    double r = 4 - (i * 0.2) + Math.sin(ticks * 0.2) * 0.5;
                    Location loc = center.clone().add(Math.cos(a) * r, Math.sin(a) * r, Math.sin(a) * r * 0.5);
                    world.spawnParticle(Particle.DUST, loc, 0, 0, 0, 0, new Particle.DustOptions(color(), 1.2f));
                }
                Particles.sphere(center, 2, Particle.DUST, color(), 8);
                for (Entity e : world.getNearbyEntities(center, 6, 6, 6)) {
                    if (e instanceof LivingEntity le && !e.equals(player)) {
                        Vector pull = center.toVector().subtract(le.getLocation().toVector()).normalize().multiply(0.25);
                        le.setVelocity(le.getVelocity().add(pull));
                        if (center.distanceSquared(le.getLocation()) < 4 && ticks == 50) {
                            le.damage(8, player);
                            le.setVelocity(new Vector(0, 2, 0));
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
        player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 100, 1));

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks > 100) {
                    for (Entity e : world.getNearbyEntities(center, 10, 15, 10)) {
                        if (e instanceof LivingEntity le && !e.equals(player)) {
                            le.setVelocity(new Vector(0, -3, 0));
                            le.damage(10, player);
                        }
                    }
                    Particles.burst(center, Particle.DUST, color(), 60, 0.8);
                    world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 0.5f);
                    cancel();
                    return;
                }
                Particles.sphere(center.clone().add(0, 5, 0), 8, Particle.DUST, color(), 20);
                for (Entity e : world.getNearbyEntities(center, 10, 10, 10)) {
                    if (e instanceof LivingEntity le && !e.equals(player)) {
                        le.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20, 2));
                    }
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
