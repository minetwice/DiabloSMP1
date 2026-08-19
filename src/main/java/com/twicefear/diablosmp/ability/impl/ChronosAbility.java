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
 * Chronos stone (time).
 * Primary: Time slow aura. Secondary: Temporal rewind (invulnerable).
 */
public class ChronosAbility extends AbstractAbility {

    public ChronosAbility(DiabloSMP plugin) {
        super(plugin, StoneType.CHRONOS);
    }

    @Override
    public void onPrimary(Player player, PlayerInteractEvent event) {
        if (primaryOnCooldown(player)) return;
        startPrimaryCooldown(player);

        Location center = player.getLocation();
        World world = center.getWorld();
        world.playSound(center, Sound.BLOCK_BEACON_ACTIVATE, 1.5f, 0.5f);

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks > 120) { cancel(); return; }
                double angle = ticks * 0.1;
                for (int i = 0; i < 12; i++) {
                    double a = (Math.PI * 2 * i / 12) + angle;
                    Location loc = center.clone().add(Math.cos(a) * 6, 1, Math.sin(a) * 6);
                    world.spawnParticle(Particle.DUST, loc, 1, 0, 0, 0, new Particle.DustOptions(color(), 1.5f));
                }
                double hand = angle * 5;
                Particles.line(center.clone().add(0, 1, 0),
                        center.clone().add(0, 1, 0).add(new Vector(Math.cos(hand) * 5, 0, Math.sin(hand) * 5)),
                        Particle.DUST, color(), 10);
                Particles.sphere(center.clone().add(0, 1, 0), 6, Particle.END_ROD, color(), 8);
                if (ticks % 10 == 0) {
                    for (Entity e : world.getNearbyEntities(center, 8, 4, 8)) {
                        if (e instanceof LivingEntity le && !e.equals(player)) {
                            le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 30, 5));
                            le.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 30, 3));
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

        Location[] saved = {player.getLocation().clone()};
        double[] health = {player.getHealth()};
        player.setMetadata("diablo_rewind", new org.bukkit.metadata.FixedMetadataValue(plugin, true));
        player.setInvulnerable(true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 3));

        new BukkitRunnable() {
            int ticks = 0;
            final Location start = player.getLocation().clone();
            @Override
            public void run() {
                if (ticks > 100) {
                    player.teleport(saved[0]);
                    player.setHealth(Math.max(player.getHealth(), health[0]));
                    player.setInvulnerable(false);
                    player.removeMetadata("diablo_rewind", plugin);
                    Particles.burst(player.getLocation(), Particle.END_ROD, color(), 60, 0.5);
                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 2f, 2f);
                    cancel();
                    return;
                }
                Location ghost = player.getLocation().clone();
                player.getWorld().spawnParticle(Particle.DUST, ghost, 5, 0.3, 0.8, 0.3,
                        new Particle.DustOptions(color(), 1.2f));
                if (ticks % 10 == 0) {
                    Particles.line(start, player.getLocation(), Particle.END_ROD, color(), 15);
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
