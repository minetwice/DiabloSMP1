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
 * Holy stone.
 * Primary: Smite pillar of light. Secondary: Healing sanctuary ring.
 */
public class HolyAbility extends AbstractAbility {

    public HolyAbility(DiabloSMP plugin) {
        super(plugin, StoneType.HOLY);
    }

    @Override
    public void onPrimary(Player player, PlayerInteractEvent event) {
        if (primaryOnCooldown(player)) return;
        startPrimaryCooldown(player);

        Location target = player.getTargetBlockExact(30).getLocation();
        World world = target.getWorld();
        world.playSound(target, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.5f, 1.5f);

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks > 30) { cancel(); return; }
                for (double y = 0; y < 10; y += 0.5) {
                    Location loc = target.clone().add(0, y, 0);
                    world.spawnParticle(Particle.END_ROD, loc, 1, 0.1, 0, 0.1, 0);
                }
                Particles.ring(target, 2 + ticks * 0.1, Particle.DUST, color(), 24);
                if (ticks == 15) {
                    for (Entity e : world.getNearbyEntities(target, 3, 6, 3)) {
                        if (e instanceof LivingEntity le) {
                            if (e.equals(player) || (le instanceof Animals)) {
                                le.heal(8);
                                le.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 1));
                            } else {
                                le.damage(10, player);
                                le.setFireTicks(0);
                            }
                        }
                    }
                    Particles.burst(target.clone().add(0, 2, 0), Particle.END_ROD, color(), 50, 0.3);
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
                Particles.ring(center, 5, Particle.DUST, color(), 40);
                Particles.spiral(center, 5, 0.1, 1, Particle.END_ROD, color(), 20);
                if (ticks % 20 == 0) {
                    for (Entity e : world.getNearbyEntities(center, 6, 3, 6)) {
                        if (e instanceof LivingEntity le) {
                            if (e.equals(player)) {
                                le.heal(4);
                                le.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 40, 1));
                            } else if (le instanceof Monster) {
                                le.damage(4, player);
                                le.setVelocity(le.getLocation().toVector().subtract(center.toVector()).normalize().multiply(0.6).setY(0.3));
                            }
                        }
                    }
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
