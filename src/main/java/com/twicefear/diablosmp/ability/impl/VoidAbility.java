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
 * Void stone.
 * Primary: Void rift that sucks entities and implodes. Secondary: Orbiting singularity.
 */
public class VoidAbility extends AbstractAbility {

    public VoidAbility(DiabloSMP plugin) {
        super(plugin, StoneType.VOID);
    }

    @Override
    public void onPrimary(Player player, PlayerInteractEvent event) {
        if (primaryOnCooldown(player)) return;
        startPrimaryCooldown(player);

        Location center = player.getLocation().add(player.getLocation().getDirection().setY(0).normalize().multiply(6));
        World world = center.getWorld();
        world.playSound(center, Sound.ENTITY_ENDERMAN_SCREAM, 1f, 0.5f);

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks > 60) {
                    Particles.burst(center, Particle.PORTAL, color(), 60, 0.5);
                    Particles.burst(center, Particle.DRAGON_BREATH, color(), 30, 0.2);
                    for (Entity e : world.getNearbyEntities(center, 4, 4, 4)) {
                        if (e instanceof LivingEntity le && !e.equals(player)) {
                            le.damage(8, player);
                            le.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 60, 1));
                        }
                    }
                    cancel();
                    return;
                }
                Particles.sphere(center, 2 - ticks * 0.02, Particle.PORTAL, color(), 20);
                Particles.orbit(center, 1.5, 1, ticks * 0.3, Particle.DRAGON_BREATH, color(), 10);
                for (Entity e : world.getNearbyEntities(center, 6, 6, 6)) {
                    if (e instanceof LivingEntity le && !e.equals(player)) {
                        Vector pull = center.toVector().subtract(le.getLocation().toVector()).normalize().multiply(0.15);
                        le.setVelocity(le.getVelocity().add(pull));
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

        new BukkitRunnable() {
            int ticks = 0;
            double angle = 0;
            final Location center = player.getLocation();
            @Override
            public void run() {
                if (ticks > 150 || !player.isOnline()) { cancel(); return; }
                angle += 0.2;
                Location singularity = player.getLocation().add(Math.cos(angle) * 5, 2, Math.sin(angle) * 5);
                Particles.sphere(singularity, 1.5, Particle.DRAGON_BREATH, color(), 15);
                Particles.sphere(singularity, 0.5, Particle.PORTAL, color(), 10);
                for (Entity e : singularity.getWorld().getNearbyEntities(singularity, 8, 8, 8)) {
                    if (e instanceof LivingEntity le && !e.equals(player)) {
                        Vector pull = singularity.toVector().subtract(le.getLocation().toVector()).normalize().multiply(0.12);
                        le.setVelocity(le.getVelocity().add(pull));
                        if (singularity.distanceSquared(le.getLocation()) < 4) {
                            le.damage(5, player);
                            le.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 40, 1));
                            le.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20, 0));
                        }
                    }
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
