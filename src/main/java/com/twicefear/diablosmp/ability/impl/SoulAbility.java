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

/**
 * Soul stone.
 * Primary: Rips souls from enemies, summoning orbiting wisps (shield).
 * Secondary: Soul harvest wave that marks enemies for bonus damage.
 */
public class SoulAbility extends AbstractAbility {

    public SoulAbility(DiabloSMP plugin) {
        super(plugin, StoneType.SOUL);
    }

    @Override
    public void onPrimary(Player player, PlayerInteractEvent event) {
        if (primaryOnCooldown(player)) return;
        startPrimaryCooldown(player);

        Location center = player.getLocation();
        World world = center.getWorld();

        new BukkitRunnable() {
            int ticks = 0;
            int wisps = 0;
            @Override
            public void run() {
                if (ticks > 60) { cancel(); return; }
                for (Entity e : world.getNearbyEntities(center, 8, 4, 8)) {
                    if (e instanceof LivingEntity le && !e.equals(player)) {
                        Particles.line(le.getLocation().add(0, 1, 0), player.getEyeLocation(),
                                Particle.SOUL, color(), 8);
                        if (ticks % 20 == 0) {
                            le.damage(4, player);
                            wisps++;
                            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 200, wisps / 2));
                        }
                    }
                }
                for (int i = 0; i < wisps; i++) {
                    double a = ticks * 0.2 + (Math.PI * 2 * i / Math.max(1, wisps));
                    Location loc = player.getLocation().add(Math.cos(a) * 2, 1, Math.sin(a) * 2);
                    world.spawnParticle(Particle.SOUL, loc, 1, 0, 0, 0, 0.02);
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
                if (ticks > 80) { cancel(); return; }
                double r = ticks * 0.3;
                Particles.ring(center, r, Particle.SOUL, color(), 40);
                Particles.ring(center, r * 0.7, Particle.SOUL_FIRE_FLAME, color(), 20);
                if (ticks == 40) {
                    for (Entity e : world.getNearbyEntities(center, 10, 5, 10)) {
                        if (e instanceof LivingEntity le && !e.equals(player)) {
                            le.setMetadata("diablo_soul_marked", new org.bukkit.metadata.FixedMetadataValue(plugin, true));
                            le.damage(5, player);
                            player.heal(3);
                            Particles.burst(le.getLocation(), Particle.SOUL, color(), 20, 0.2);
                        }
                    }
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Entity e : world.getEntities()) {
                e.removeMetadata("diablo_soul_marked", plugin);
            }
        }, 100L);
    }
}
