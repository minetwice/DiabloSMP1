package com.twicefear.diablosmp.abilities;

import com.twicefear.diablosmp.DiabloSMP;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class FrostbiteAbility {

    private final DiabloSMP plugin;

    public FrostbiteAbility(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    public void primary(Player player) {
        // Ice Spikes Wave - expanding ice spikes from player
        Location origin = player.getLocation();
        player.sendMessage("§b§lIce Spikes erupt!");
        player.getWorld().playSound(origin, Sound.BLOCK_GLASS_BREAK, 1.5f, 0.6f);

        new BukkitRunnable() {
            int wave = 0;
            @Override
            public void run() {
                if (wave >= 6) {
                    cancel();
                    return;
                }

                double radius = 2 + wave * 1.5;
                for (int i = 0; i < 16; i++) {
                    double angle = (Math.PI * 2 / 16) * i;
                    Location loc = origin.clone().add(Math.cos(angle) * radius, 0, Math.sin(angle) * radius);
                    Block b = loc.getBlock();

                    // Temporary ice spike visual
                    loc.getWorld().spawnParticle(Particle.SNOWFLAKE, loc.clone().add(0, 1, 0), 15, 0.3, 1, 0.3, 0.02);
                    loc.getWorld().spawnParticle(Particle.BLOCK, loc.clone().add(0, 0.5, 0), 10, 0.2, 0.5, 0.2, 0, Material.BLUE_ICE.createBlockData());

                    for (Entity e : loc.getWorld().getNearbyEntities(loc, 1.2, 2, 1.2)) {
                        if (e instanceof Player p && p != player) {
                            p.damage(3.5, player);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 2));
                            p.setVelocity(new Vector(0, 0.4, 0));
                        }
                    }
                }
                wave++;
            }
        }.runTaskTimer(plugin, 0L, 4L);
    }

    public void secondary(Player player) {
        // Frozen Tomb - encase nearby enemies in ice for a short time
        Location center = player.getLocation();
        player.sendMessage("§b§lFrozen Tomb!");

        List<Player> victims = new ArrayList<>();
        for (Entity e : center.getWorld().getNearbyEntities(center, 6, 3, 6)) {
            if (e instanceof Player p && p != player) {
                victims.add(p);
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 4));
                p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 100, 128)); // anti jump
                p.sendMessage("§bYou have been frozen!");
            }
        }

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= 100) {
                    cancel();
                    return;
                }

                for (Player p : victims) {
                    if (!p.isOnline()) continue;
                    p.getWorld().spawnParticle(Particle.SNOWFLAKE, p.getLocation().add(0, 1, 0), 20, 0.4, 0.8, 0.4, 0.02);
                    p.getWorld().spawnParticle(Particle.BLOCK, p.getLocation().add(0, 1, 0), 8, 0.3, 0.6, 0.3, 0, Material.ICE.createBlockData());

                    if (ticks % 20 == 0) {
                        p.damage(1.5, player);
                    }
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
