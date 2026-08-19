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

public class VoidwalkerAbility {

    private final DiabloSMP plugin;

    public VoidwalkerAbility(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    public void primary(Player player) {
        // Teleport + Void Damage
        Location target = player.getTargetBlockExact(25) != null
                ? player.getTargetBlockExact(25).getLocation().add(0.5, 1, 0.5)
                : player.getLocation().add(player.getLocation().getDirection().multiply(15));

        player.sendMessage("§8§lVoid Step!");
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.5f, 0.6f);

        // Trail
        Location from = player.getLocation().clone();
        player.teleport(target);

        new BukkitRunnable() {
            int i = 0;
            @Override
            public void run() {
                if (i >= 10) {
                    cancel();
                    return;
                }
                Location point = from.clone().add(target.toVector().subtract(from.toVector()).multiply(i / 10.0));
                point.getWorld().spawnParticle(Particle.REVERSE_PORTAL, point, 8, 0.2, 0.4, 0.2, 0.02);
                point.getWorld().spawnParticle(Particle.PORTAL, point, 5, 0.15, 0.3, 0.15, 0.01);
                i++;
            }
        }.runTaskTimer(plugin, 0L, 1L);

        // Damage on arrival
        for (Entity e : target.getWorld().getNearbyEntities(target, 3, 3, 3)) {
            if (e instanceof Player p && p != player) {
                p.damage(7.0, player);
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
                p.getWorld().spawnParticle(Particle.REVERSE_PORTAL, p.getLocation().add(0, 1, 0), 20, 0.4, 0.6, 0.4, 0.05);
            }
        }
    }

    public void secondary(Player player) {
        // Void Collapse
        Location center = player.getLocation();
        player.sendMessage("§8§lVoid Collapse!");

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= 100) {
                    // Final collapse damage
                    for (Entity e : center.getWorld().getNearbyEntities(center, 6, 4, 6)) {
                        if (e instanceof Player p && p != player) {
                            p.damage(8.0, player);
                            Vector pull = center.toVector().subtract(p.getLocation().toVector()).normalize().multiply(1.5);
                            p.setVelocity(pull);
                        }
                    }
                    center.getWorld().spawnParticle(Particle.REVERSE_PORTAL, center, 80, 2, 2, 2, 0.15);
                    center.getWorld().playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 2f, 0.5f);
                    cancel();
                    return;
                }

                double radius = 6 - (ticks * 0.03);
                for (int i = 0; i < 20; i++) {
                    double angle = (Math.PI * 2 / 20) * i + ticks * 0.2;
                    Location p = center.clone().add(Math.cos(angle) * radius, 1, Math.sin(angle) * radius);
                    p.getWorld().spawnParticle(Particle.REVERSE_PORTAL, p, 2, 0.1, 0.2, 0.1, 0.01);
                }

                for (Entity e : center.getWorld().getNearbyEntities(center, 7, 4, 7)) {
                    if (e instanceof Player p && p != player) {
                        Vector pull = center.toVector().subtract(p.getLocation().toVector()).normalize().multiply(0.25);
                        p.setVelocity(p.getVelocity().add(pull));
                        if (ticks % 10 == 0) p.damage(1.5, player);
                    }
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
