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
 * Nature stone.
 * Primary: Entangling roots. Secondary: Healing grove.
 */
public class NatureAbility extends AbstractAbility {

    public NatureAbility(DiabloSMP plugin) {
        super(plugin, StoneType.NATURE);
    }

    @Override
    public void onPrimary(Player player, PlayerInteractEvent event) {
        if (primaryOnCooldown(player)) return;
        startPrimaryCooldown(player);

        Location center = player.getLocation().add(player.getLocation().getDirection().setY(0).normalize().multiply(6));
        center.setY(center.getWorld().getHighestBlockYAt(center));
        World world = center.getWorld();

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks > 40) { cancel(); return; }
                for (int i = 0; i < 8; i++) {
                    double a = (Math.PI * 2 * i / 8) + ticks * 0.1;
                    double r = 0.5 + ticks * 0.1;
                    Location loc = center.clone().add(Math.cos(a) * r, ticks * 0.2, Math.sin(a) * r);
                    world.spawnParticle(Particle.HAPPY_VILLAGER, loc, 2, 0.1, 0.2, 0.1, 0);
                    world.spawnParticle(Particle.DUST, loc, 1, 0, 0, 0, new Particle.DustOptions(color(), 1.2f));
                }
                Particles.spiral(center, 1.5, 3, 2, Particle.DUST, color(), 20);
                if (ticks % 10 == 0) {
                    for (Entity e : world.getNearbyEntities(center, 3, 3, 3)) {
                        if (e instanceof LivingEntity le && !e.equals(player)) {
                            le.damage(3, player);
                            le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 3));
                            le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 1));
                            le.setVelocity(new Vector(0, 0, 0));
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

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks > 200) { cancel(); return; }
                Particles.ring(center, 5, Particle.HAPPY_VILLAGER, color(), 20);
                Particles.spiral(center, 4, 0.5, 1, Particle.DUST, color(), 15);
                if (ticks % 5 == 0) {
                    Location leaf = center.clone().add((Math.random() - 0.5) * 8, 5, (Math.random() - 0.5) * 8);
                    world.spawnParticle(Particle.DUST, leaf, 1, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(60, 200, 80), 1.5f));
                }
                if (ticks % 20 == 0) {
                    for (Entity e : world.getNearbyEntities(center, 6, 4, 6)) {
                        if (e instanceof LivingEntity le) {
                            if (e.equals(player) || le instanceof Animals) {
                                le.heal(5);
                                le.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 0));
                            } else if (le instanceof Monster || le instanceof Player) {
                                if (!e.equals(player)) {
                                    le.damage(3, player);
                                    le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 40, 1));
                                }
                            }
                        }
                    }
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
