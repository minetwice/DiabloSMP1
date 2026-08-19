package me.twicefear.diablosmp.ability.impl;

import me.twicefear.diablosmp.DiabloSMP;
import me.twicefear.diablosmp.ability.DiabloAbility;
import me.twicefear.diablosmp.stone.StoneType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class FrostMonarchAbility implements DiabloAbility {

    private final DiabloSMP plugin;

    public FrostMonarchAbility(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public StoneType getStoneType() {
        return StoneType.FROST_MONARCH;
    }

    @Override
    public boolean isSecondary() {
        return false;
    }

    @Override
    public void execute(Player player) {
        // Primary: Glacial Rampart
        player.sendMessage(ChatColor.AQUA + "[Frost Monarch] " + ChatColor.WHITE + "GLACIAL RAMPART!");
        Location loc = player.getLocation();
        loc.getWorld().playSound(loc, Sound.BLOCK_GLASS_BREAK, 1.2f, 0.5f);

        // Spawn ice wall in front of player
        Location wallCenter = loc.clone().add(loc.getDirection().normalize().multiply(3));
        for (int x = -2; x <= 2; x++) {
            for (int y = 0; y <= 3; y++) {
                Location blockLoc = wallCenter.clone().add(x, y, 0);
                if (blockLoc.getBlock().isEmpty()) {
                    blockLoc.getBlock().setType(Material.PACKED_ICE);
                }
            }
        }

        wallCenter.getWorld().spawnParticle(Particle.SNOWFLAKE, wallCenter, 100, 2, 2, 2, 0.1);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (int x = -2; x <= 2; x++) {
                    for (int y = 0; y <= 3; y++) {
                        Location blockLoc = wallCenter.clone().add(x, y, 0);
                        if (blockLoc.getBlock().getType() == Material.PACKED_ICE) {
                            blockLoc.getBlock().setType(Material.AIR);
                        }
                    }
                }
            }
        }.runTaskLater(plugin, 100L); // 5 seconds duration
    }

    public void executeSecondary(Player player) {
        // Secondary: Absolute Zero Domain
        player.sendMessage(ChatColor.DARK_AQUA + "[Frost Monarch] " + ChatColor.AQUA + "ABSOLUTE ZERO DOMAIN!");
        Location center = player.getLocation();

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= 100) { // 5s
                    cancel();
                    return;
                }
                ticks++;

                double radius = 8.0;
                for (int i = 0; i < 360; i += 15) {
                    double rad = Math.toRadians(i);
                    Location pLoc = center.clone().add(radius * Math.cos(rad), 0.5, radius * Math.sin(rad));
                    center.getWorld().spawnParticle(Particle.SNOWFLAKE, pLoc, 3, 0.1, 0.5, 0.1, 0.02);
                }

                for (Entity e : center.getWorld().getNearbyEntities(center, radius, 4, radius)) {
                    if (e != player && e instanceof LivingEntity le) {
                        le.damage(2.0, player);
                        le.setFreezeTicks(100);
                        le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 3));
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }
}
