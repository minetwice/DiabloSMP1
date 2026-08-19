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

public class AbyssalAbility {

    private final DiabloSMP plugin;

    public AbyssalAbility(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    public void primary(Player player) {
        // Abyss Pull
        Location center = player.getLocation();
        player.sendMessage("§3§lAbyss Pull!");
        player.getWorld().playSound(center, Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1.2f, 0.7f);

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= 80) {
                    cancel();
                    return;
                }

                double radius = 8;
                for (int i = 0; i < 16; i++) {
                    double angle = (Math.PI * 2 / 16) * i + ticks * 0.15;
                    double r = radius - (ticks * 0.05);
                    Location p = center.clone().add(Math.cos(angle) * r, 0.5, Math.sin(angle) * r);
                    p.getWorld().spawnParticle(Particle.SOUL, p, 2, 0.1, 0.2, 0.1, 0.01);
                    p.getWorld().spawnParticle(Particle.BUBBLE_COLUMN_UP, p, 1, 0.05, 0.1, 0.05, 0);
                }

                for (Entity e : center.getWorld().getNearbyEntities(center, radius, 4, radius)) {
                    if (e instanceof Player p && p != player) {
                        Vector pull = center.toVector().subtract(p.getLocation().toVector()).normalize().multiply(0.4);
                        p.setVelocity(p.getVelocity().add(pull));
                        p.damage(1.2, player);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20, 1));
                    }
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public void secondary(Player player) {
        // Drowning Realm
        Location center = player.getLocation();
        player.sendMessage("§3§lDrowning Realm!");

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= 160) {
                    cancel();
                    return;
                }

                double radius = 6;
                for (int i = 0; i < 18; i++) {
                    double angle = (Math.PI * 2 / 18) * i;
                    Location p = center.clone().add(Math.cos(angle) * radius, 1, Math.sin(angle) * radius);
                    p.getWorld().spawnParticle(Particle.BUBBLE_POP, p, 2, 0.2, 0.4, 0.2, 0.02);
                    p.getWorld().spawnParticle(Particle.SOUL, p, 1, 0.1, 0.2, 0.1, 0);
                }

                for (Entity e : center.getWorld().getNearbyEntities(center, radius, 4, radius)) {
                    if (e instanceof Player p && p != player) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 10, 0)); // ironic
                        p.damage(1.8, player);
                        p.setVelocity(p.getVelocity().add(new Vector(0, -0.1, 0)));
                        if (ticks % 20 == 0) {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 0));
                        }
                    }
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
