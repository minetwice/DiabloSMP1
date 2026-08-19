package com.twicefear.diablosmp.ability.impl;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.stone.StoneType;
import com.twicefear.diablosmp.util.Particles;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Earthquake stone.
 * Primary: Lift blocks, cursor-controlled for 8s.
 * Secondary: 4 pillars -> shadow domain (10s).
 */
public class EarthquakeAbility extends AbstractAbility {

    public EarthquakeAbility(DiabloSMP plugin) {
        super(plugin, StoneType.EARTHQUAKE);
    }

    @Override
    public void onPrimary(Player player, PlayerInteractEvent event) {
        if (primaryOnCooldown(player)) return;
        startPrimaryCooldown(player);

        Location target = player.getLocation().clone().add(player.getLocation().getDirection().setY(0).normalize().multiply(4));
        target.setY(target.getWorld().getHighestBlockYAt(target) + 1);

        World world = target.getWorld();
        Set<Location> blockLocs = new HashSet<>();
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                blockLocs.add(target.clone().add(x, -1, z));
            }
        }

        List<FallingBlock> cluster = new ArrayList<>();
        BlockData stoneData = Material.STONE.createBlockData();
        for (Location loc : blockLocs) {
            FallingBlock fb = world.spawnFallingBlock(loc, stoneData);
            fb.setDropItem(false);
            fb.setGravity(false);
            fb.setHurtEntities(false);
            cluster.add(fb);
        }

        List<LivingEntity> riders = new ArrayList<>();
        for (Entity e : world.getNearbyEntities(target, 3, 3, 3)) {
            if (e instanceof LivingEntity le && !e.equals(player)) {
                le.setVelocity(new Vector(0, 0.5, 0));
                riders.add(le);
            }
        }

        new BukkitRunnable() {
            int ticks = 0;
            double angle = 0;
            @Override
            public void run() {
                if (ticks > 8 * 20 || !player.isOnline()) {
                    for (FallingBlock fb : cluster) {
                        if (fb.isValid()) { fb.setGravity(true); fb.remove(); }
                    }
                    cancel();
                    return;
                }
                Location cursor = player.getEyeLocation().add(player.getLocation().getDirection().multiply(8));
                angle += 0.3;
                double hoverY = 2 + Math.sin(angle) * 0.3;
                for (int i = 0; i < cluster.size(); i++) {
                    FallingBlock fb = cluster.get(i);
                    if (!fb.isValid()) continue;
                    Location base = cursor.clone().add(Math.cos(angle + i) * 1.5, hoverY - 2, Math.sin(angle + i) * 1.5);
                    Vector vel = base.toVector().subtract(fb.getLocation().toVector()).multiply(0.3);
                    fb.setVelocity(vel);
                    for (LivingEntity r : riders) {
                        if (r.isValid() && r.getLocation().distanceSquared(fb.getLocation()) < 9) {
                            r.setVelocity(r.getVelocity().add(new Vector(0, 0.1, 0)));
                        }
                    }
                }
                Particles.sphere(cursor.clone().add(0, hoverY, 0), 2.2, Particle.DUST, color(), 12);
                Particles.orbit(cursor.clone().add(0, hoverY, 0), 2.5, 0, angle, Particle.CRIT, null, 8);
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
        Location[] pillars = new Location[4];
        double r = 4;
        for (int i = 0; i < 4; i++) {
            double a = (Math.PI / 2) * i;
            pillars[i] = center.clone().add(Math.cos(a) * r, 0, Math.sin(a) * r);
        }

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks > 10 * 20) { cancel(); return; }
                for (Location pillar : pillars) {
                    Particles.doubleHelix(pillar, 0.8, 6, 3, Particle.DUST, color(), 20);
                    Particles.sphere(pillar.clone().add(0, 3, 0), 1.2, Particle.PORTAL, null, 10);
                }
                if (ticks % 10 == 0) {
                    for (Location pillar : pillars) {
                        for (Entity e : world.getNearbyEntities(pillar, 1.5, 6, 1.5)) {
                            if (e instanceof LivingEntity le && !e.equals(player) && !le.hasMetadata("diablo_domain")) {
                                pullIntoDomain(player, le, pillar.clone());
                            }
                        }
                    }
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void pullIntoDomain(Player caster, LivingEntity entity, Location origin) {
        entity.setMetadata("diablo_domain", new org.bukkit.metadata.FixedMetadataValue(plugin, true));
        Location originSaved = origin.clone();
        Location domainLoc = origin.clone().add(0, -200, 0);
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks == 0) {
                    entity.teleport(domainLoc);
                    caster.teleport(domainLoc.clone().add(0, 0, 3));
                }
                Location ring = entity.getLocation();
                for (int i = 0; i < 4; i++) {
                    double a = (Math.PI / 2) * i;
                    Location corner = ring.clone().add(Math.cos(a) * 6, 0, Math.sin(a) * 6);
                    Particles.spiral(corner, 0.3, 6, 1, Particle.DUST, Color.fromRGB(80, 0, 120), 30);
                }
                Particles.sphere(entity.getLocation(), 8, Particle.SQUID_INK, null, 30);
                if (ticks % 20 == 0) {
                    caster.addPotionEffect(new org.bukkit.potion.PotionEffect(
                            org.bukkit.potion.PotionEffectType.REGENERATION, 40, 1));
                    entity.addPotionEffect(new org.bukkit.potion.PotionEffect(
                            org.bukkit.potion.PotionEffectType.SLOWNESS, 40, 2));
                }
                if (ticks >= 10 * 20) {
                    entity.teleport(originSaved);
                    caster.teleport(originSaved.clone().add(0, 0, 3));
                    entity.removeMetadata("diablo_domain", plugin);
                    cancel();
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
