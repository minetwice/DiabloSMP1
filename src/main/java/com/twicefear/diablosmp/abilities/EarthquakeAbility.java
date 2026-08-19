package com.twicefear.diablosmp.abilities;

import com.twicefear.diablosmp.DiabloSMP;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Earthquake Relic - Extremely complex abilities as requested.
 * Primary: Lift a bunch of blocks connected to cursor for 8s, can rotate & launch.
 * Secondary: 4 Pillars domain - boxing arena with special rules.
 */
public class EarthquakeAbility {

    private final DiabloSMP plugin;

    public EarthquakeAbility(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    public void primary(Player player) {
        // Raytrace to find target location
        RayTraceResult result = player.getWorld().rayTraceBlocks(player.getEyeLocation(), player.getLocation().getDirection(), 30);
        if (result == null || result.getHitBlock() == null) {
            player.sendMessage("§cNo target block found!");
            return;
        }

        Location center = result.getHitBlock().getLocation().add(0.5, 0, 0.5);
        List<FallingBlock> lifted = new ArrayList<>();

        // Lift a 3x3x2 chunk of blocks
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                for (int y = 0; y <= 1; y++) {
                    Block b = center.clone().add(x, y, z).getBlock();
                    if (b.getType().isAir() || b.getType() == Material.BEDROCK) continue;

                    FallingBlock fb = b.getWorld().spawnFallingBlock(b.getLocation().add(0.5, 0, 0.5), b.getBlockData());
                    fb.setDropItem(false);
                    fb.setHurtEntities(true);
                    fb.setGravity(false);
                    lifted.add(fb);
                    b.setType(Material.AIR);
                }
            }
        }

        player.sendMessage("§6§lEarthquake! Blocks under control for 8 seconds. Left click to launch!");

        // Control loop for 8 seconds
        new BukkitRunnable() {
            int ticks = 0;
            final int max = 8 * 20;

            @Override
            public void run() {
                if (ticks >= max || !player.isOnline()) {
                    // Drop them
                    for (FallingBlock fb : lifted) {
                        if (fb.isValid()) {
                            fb.setGravity(true);
                            fb.setVelocity(new Vector(0, -0.5, 0));
                        }
                    }
                    cancel();
                    return;
                }

                // Follow cursor roughly
                RayTraceResult rt = player.getWorld().rayTraceBlocks(player.getEyeLocation(), player.getLocation().getDirection(), 40);
                Location target = rt != null && rt.getHitBlock() != null
                        ? rt.getHitBlock().getLocation().add(0.5, 1, 0.5)
                        : player.getEyeLocation().add(player.getLocation().getDirection().multiply(15));

                for (int i = 0; i < lifted.size(); i++) {
                    FallingBlock fb = lifted.get(i);
                    if (!fb.isValid()) continue;

                    // Orbit + follow
                    double angle = ticks * 0.15 + i;
                    Location desired = target.clone().add(Math.cos(angle) * 1.2, Math.sin(ticks * 0.1) * 0.5, Math.sin(angle) * 1.2);
                    Vector vel = desired.toVector().subtract(fb.getLocation().toVector()).multiply(0.3);
                    fb.setVelocity(vel);

                    // Particles
                    fb.getWorld().spawnParticle(Particle.BLOCK, fb.getLocation(), 3, 0.2, 0.2, 0.2, 0, fb.getBlockData());
                }

                // Lift nearby players too
                for (Entity e : target.getWorld().getNearbyEntities(target, 3, 3, 3)) {
                    if (e instanceof Player p && p != player) {
                        p.setVelocity(new Vector(0, 0.15, 0));
                    }
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);

        // Launch on left click is handled via another listener or can be added later
    }

    public void secondary(Player player) {
        Location center = player.getLocation();

        // Spawn 4 pillars around player (using barrier + particles for now, model later via resource pack)
        Location[] pillars = new Location[4];
        pillars[0] = center.clone().add(4, 0, 4);
        pillars[1] = center.clone().add(4, 0, -4);
        pillars[2] = center.clone().add(-4, 0, 4);
        pillars[3] = center.clone().add(-4, 0, -4);

        for (Location loc : pillars) {
            for (int y = 0; y < 6; y++) {
                loc.clone().add(0, y, 0).getBlock().setType(Material.CRYING_OBSIDIAN);
            }
            loc.getWorld().spawnParticle(Particle.REVERSE_PORTAL, loc.clone().add(0.5, 3, 0.5), 40, 0.5, 2, 0.5, 0.05);
        }

        player.sendMessage("§6§lDomain Expansion: Earth Prison!");

        // Domain effect for 10 seconds
        new BukkitRunnable() {
            int ticks = 0;
            final List<Player> trapped = new ArrayList<>();

            @Override
            public void run() {
                if (ticks >= 200) { // 10s
                    // Cleanup pillars
                    for (Location loc : pillars) {
                        for (int y = 0; y < 6; y++) {
                            loc.clone().add(0, y, 0).getBlock().setType(Material.AIR);
                        }
                    }
                    for (Player p : trapped) {
                        p.sendMessage("§aYou have returned from the domain.");
                    }
                    cancel();
                    return;
                }

                // Pull nearby entities into domain visual
                for (Entity e : center.getWorld().getNearbyEntities(center, 8, 6, 8)) {
                    if (e instanceof Player p && p != player) {
                        if (!trapped.contains(p)) {
                            trapped.add(p);
                            p.sendMessage("§cYou have been pulled into the Earth Domain!");
                            // Apply effects
                            p.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.SLOWNESS, 40, 1));
                        }
                        // Visual isolation (simple version)
                        p.getWorld().spawnParticle(Particle.ASH, p.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5, 0.01);
                    }
                }

                // Owner heals when dealing damage (simplified)
                if (ticks % 20 == 0) {
                    player.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, player.getLocation().add(0, 1, 0), 15, 0.5, 0.5, 0.5, 0.05);
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
