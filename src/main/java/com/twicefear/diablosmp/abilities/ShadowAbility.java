package com.twicefear.diablosmp.abilities;

import com.twicefear.diablosmp.DiabloSMP;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class ShadowAbility {

    private final DiabloSMP plugin;

    public ShadowAbility(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    public void primary(Player player) {
        // Shadow Dash + Backstab style
        Vector dir = player.getLocation().getDirection().normalize().multiply(8);
        Location dest = player.getLocation().add(dir);

        // Particle trail
        new BukkitRunnable() {
            int i = 0;
            Location current = player.getLocation().clone();
            @Override
            public void run() {
                if (i >= 8) {
                    player.teleport(dest);
                    player.getWorld().playSound(dest, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 0.8f);
                    player.getWorld().spawnParticle(Particle.SMOKE, dest, 40, 0.5, 1, 0.5, 0.05);

                    // Damage nearby on arrival
                    for (Entity e : dest.getWorld().getNearbyEntities(dest, 2.5, 2.5, 2.5)) {
                        if (e instanceof Player p && p != player) {
                            p.damage(6.0, player);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
                        }
                    }
                    cancel();
                    return;
                }
                current.add(dir.clone().multiply(0.125));
                current.getWorld().spawnParticle(Particle.SMOKE, current, 5, 0.1, 0.3, 0.1, 0.01);
                current.getWorld().spawnParticle(Particle.SQUID_INK, current, 2, 0.05, 0.1, 0.05, 0);
                i++;
            }
        }.runTaskTimer(plugin, 0L, 1L);

        player.sendMessage("§8§lShadow Dash!");
    }

    public void secondary(Player player) {
        // Night Domain - darkness + weakness for enemies
        Location center = player.getLocation();
        player.sendMessage("§8§lNight Domain expands...");

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= 200) {
                    cancel();
                    return;
                }

                double radius = 8;
                for (int i = 0; i < 16; i++) {
                    double angle = (Math.PI * 2 / 16) * i + ticks * 0.05;
                    Location p = center.clone().add(Math.cos(angle) * radius, 1, Math.sin(angle) * radius);
                    p.getWorld().spawnParticle(Particle.SMOKE, p, 3, 0.2, 0.5, 0.2, 0.01);
                    p.getWorld().spawnParticle(Particle.SQUID_INK, p, 1, 0.1, 0.2, 0.1, 0);
                }

                for (Entity e : center.getWorld().getNearbyEntities(center, radius, 4, radius)) {
                    if (e instanceof Player p && p != player) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 30, 0));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 30, 0));
                        p.damage(0.6, player);
                    }
                }

                // Owner gets speed in domain
                if (ticks % 20 == 0) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 30, 1));
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
