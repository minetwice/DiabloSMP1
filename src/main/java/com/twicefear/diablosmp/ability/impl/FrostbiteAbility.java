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
 * Frostbite stone.
 * Primary: Piercing ice shard. Secondary: Ice sphere shield.
 */
public class FrostbiteAbility extends AbstractAbility {

    public FrostbiteAbility(DiabloSMP plugin) {
        super(plugin, StoneType.FROSTBITE);
    }

    @Override
    public void onPrimary(Player player, PlayerInteractEvent event) {
        if (primaryOnCooldown(player)) return;
        startPrimaryCooldown(player);

        Location start = player.getEyeLocation();
        Vector dir = start.getDirection().multiply(1.5);
        World world = start.getWorld();
        Location[] cur = {start.clone()};

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks > 40 || cur[0].getBlock().getType().isSolid()) {
                    Particles.burst(cur[0], Particle.SNOWFLAKE, color(), 30, 0.2);
                    cancel();
                    return;
                }
                cur[0].add(dir);
                world.spawnParticle(Particle.SNOWFLAKE, cur[0], 5, 0.1, 0.1, 0.1, 0.02);
                Particles.line(cur[0].clone().subtract(dir), cur[0], Particle.DUST, color(), 4);
                for (Entity e : world.getNearbyEntities(cur[0], 1.5, 1.5, 1.5)) {
                    if (e instanceof LivingEntity le && !e.equals(player)) {
                        le.damage(5, player);
                        le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 3));
                        le.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 100, 2));
                        Particles.burst(le.getLocation(), Particle.SNOWFLAKE, color(), 20, 0.2);
                        cancel();
                        return;
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
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 200, 4));

        new BukkitRunnable() {
            int ticks = 0;
            double angle = 0;
            @Override
            public void run() {
                if (ticks > 200) { cancel(); return; }
                angle += 0.4;
                Particles.sphere(center.clone().add(0, 1, 0), 3, Particle.SNOWFLAKE, color(), 15);
                for (int i = 0; i < 6; i++) {
                    double a = angle + (Math.PI * 2 * i / 6);
                    Location shard = center.clone().add(Math.cos(a) * 3, 1, Math.sin(a) * 3);
                    world.spawnParticle(Particle.ITEM_SNOWBALL, shard, 0, 0, 0, 0, null);
                    Particles.line(center.clone().add(0, 1, 0), shard, Particle.DUST, color(), 3);
                }
                if (ticks % 20 == 0) {
                    for (Entity e : world.getNearbyEntities(center, 5, 5, 5)) {
                        if (e instanceof LivingEntity le && !e.equals(player)) {
                            le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 5));
                            le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 2));
                            le.damage(3, player);
                        }
                    }
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
