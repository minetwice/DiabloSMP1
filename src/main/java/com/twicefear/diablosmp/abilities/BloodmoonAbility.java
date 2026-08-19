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

public class BloodmoonAbility {

    private final DiabloSMP plugin;

    public BloodmoonAbility(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    public void primary(Player player) {
        // Life Steal Aura
        player.sendMessage("§4§lBlood Aura activated!");
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_AMBIENT, 1f, 0.7f);

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= 120 || !player.isOnline()) {
                    cancel();
                    return;
                }

                Location center = player.getLocation();
                for (int i = 0; i < 12; i++) {
                    double angle = (Math.PI * 2 / 12) * i + ticks * 0.1;
                    Location p = center.clone().add(Math.cos(angle) * 2.5, 1, Math.sin(angle) * 2.5);
                    p.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, p, 2, 0.1, 0.2, 0.1, 0);
                    p.getWorld().spawnParticle(Particle.DUST, p, 1, 0.1, 0.1, 0.1, 0, new org.bukkit.Particle.DustOptions(org.bukkit.Color.RED, 1.2f));
                }

                for (Entity e : center.getWorld().getNearbyEntities(center, 4, 3, 4)) {
                    if (e instanceof Player p && p != player) {
                        p.damage(1.8, player);
                        player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + 1.2));
                        p.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, p.getLocation().add(0, 1, 0), 5, 0.2, 0.3, 0.2, 0);
                    }
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public void secondary(Player player) {
        // Blood Arena
        Location center = player.getLocation();
        player.sendMessage("§4§lBlood Arena!");

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= 180) {
                    cancel();
                    return;
                }

                double radius = 7;
                for (int i = 0; i < 18; i++) {
                    double angle = (Math.PI * 2 / 18) * i;
                    Location p = center.clone().add(Math.cos(angle) * radius, 0.2, Math.sin(angle) * radius);
                    p.getWorld().spawnParticle(Particle.DUST, p, 2, 0.1, 0.1, 0.1, 0, new org.bukkit.Particle.DustOptions(org.bukkit.Color.fromRGB(120, 0, 0), 1.5f));
                }

                for (Entity e : center.getWorld().getNearbyEntities(center, radius, 4, radius)) {
                    if (e instanceof Player p && p != player) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 30, 0));
                        if (ticks % 20 == 0) {
                            p.damage(2.5, player);
                            player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + 1.5));
                        }
                    }
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
