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
 * Plague stone.
 * Primary: Spews a plague cloud that infects and spreads. Secondary: Pandemic zone.
 */
public class PlagueAbility extends AbstractAbility {

    public PlagueAbility(DiabloSMP plugin) {
        super(plugin, StoneType.PLAGUE);
    }

    @Override
    public void onPrimary(Player player, PlayerInteractEvent event) {
        if (primaryOnCooldown(player)) return;
        startPrimaryCooldown(player);

        Location start = player.getEyeLocation();
        Vector dir = start.getDirection().multiply(1);
        World world = start.getWorld();
        Location[] cur = {start.clone()};

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks > 60) { cancel(); return; }
                cur[0].add(dir.multiply(0.95));
                for (int i = 0; i < 5; i++) {
                    Location loc = cur[0].clone().add(
                            (Math.random() - 0.5) * 2, (Math.random() - 0.5) * 2, (Math.random() - 0.5) * 2);
                    world.spawnParticle(Particle.SPORE_BLOSSOM_AIR, loc, 1, 0, 0, 0, 0);
                    world.spawnParticle(Particle.DUST, loc, 1, 0, 0, 0, new Particle.DustOptions(color(), 1.5f));
                }
                world.spawnParticle(Particle.ANGRY_VILLAGER, cur[0], 1, 0.5, 0.5, 0.5, 0);
                for (Entity e : world.getNearbyEntities(cur[0], 3, 3, 3)) {
                    if (e instanceof LivingEntity le && !e.equals(player) && !le.hasMetadata("diablo_plagued")) {
                        le.setMetadata("diablo_plagued", new org.bukkit.metadata.FixedMetadataValue(plugin, true));
                        infect(le, player, 80);
                    }
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Entity e : world.getEntities()) e.removeMetadata("diablo_plagued", plugin);
        }, 100L);
    }

    private void infect(LivingEntity entity, Player caster, int duration) {
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks > duration || !entity.isValid()) {
                    entity.removeMetadata("diablo_plagued", plugin);
                    cancel();
                    return;
                }
                entity.damage(2, caster);
                entity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 40, 1));
                entity.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 40, 0));
                entity.getWorld().spawnParticle(Particle.SPORE_BLOSSOM_AIR, entity.getLocation(), 3, 0.3, 0.5, 0.3, 0);
                if (ticks % 20 == 0) {
                    for (Entity e : entity.getWorld().getNearbyEntities(entity.getLocation(), 3, 3, 3)) {
                        if (e instanceof LivingEntity le && !e.equals(entity) && !e.equals(caster)
                                && !le.hasMetadata("diablo_plagued")) {
                            le.setMetadata("diablo_plagued", new org.bukkit.metadata.FixedMetadataValue(plugin, true));
                            infect(le, caster, 40);
                        }
                    }
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 5L);
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
                if (ticks > 200) { cancel(); return; }
                radius = Math.min(10, radius + 0.05);
                for (int i = 0; i < 10; i++) {
                    double a = Math.random() * Math.PI * 2;
                    double r = Math.random() * radius;
                    Location loc = center.clone().add(Math.cos(a) * r, Math.random() * 3, Math.sin(a) * r);
                    world.spawnParticle(Particle.SPORE_BLOSSOM_AIR, loc, 1, 0, 0, 0, 0);
                    world.spawnParticle(Particle.DUST, loc, 1, 0, 0, 0, new Particle.DustOptions(color(), 1.3f));
                }
                Particles.ring(center, radius, Particle.DUST, color(), 30);
                if (ticks % 20 == 0) {
                    for (Entity e : world.getNearbyEntities(center, radius, 4, radius)) {
                        if (e instanceof LivingEntity le && !e.equals(player)) {
                            le.damage(3, player);
                            le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 60, 1));
                            le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 1));
                            le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 1));
                            le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 1));
                        }
                    }
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
