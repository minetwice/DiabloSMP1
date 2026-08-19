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

import java.util.Random;

public class ChaosAbility {

    private final DiabloSMP plugin;
    private final Random random = new Random();

    public ChaosAbility(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    public void primary(Player player) {
        // Random Ability Burst
        player.sendMessage("§5§lChaos Burst!");
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1.5f);

        Location center = player.getLocation();
        for (int i = 0; i < 8; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = 2 + random.nextDouble() * 5;
            Location loc = center.clone().add(Math.cos(angle) * dist, 1, Math.sin(angle) * dist);

            loc.getWorld().spawnParticle(Particle.PORTAL, loc, 25, 0.5, 0.8, 0.5, 0.1);
            loc.getWorld().spawnParticle(Particle.REVERSE_PORTAL, loc, 15, 0.3, 0.5, 0.3, 0.05);

            for (Entity e : loc.getWorld().getNearbyEntities(loc, 2.5, 2.5, 2.5)) {
                if (e instanceof Player p && p != player) {
                    p.damage(3.5 + random.nextDouble() * 3, player);
                    // Random effect
                    PotionEffectType[] effects = {PotionEffectType.SLOWNESS, PotionEffectType.WEAKNESS, PotionEffectType.POISON, PotionEffectType.BLINDNESS, PotionEffectType.LEVITATION};
                    p.addPotionEffect(new PotionEffect(effects[random.nextInt(effects.length)], 60, random.nextInt(2)));
                }
            }
        }
    }

    public void secondary(Player player) {
        // Chaos Domain
        Location center = player.getLocation();
        player.sendMessage("§5§lChaos Domain!");

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= 160) {
                    cancel();
                    return;
                }

                double radius = 7;
                for (int i = 0; i < 12; i++) {
                    double angle = (Math.PI * 2 / 12) * i + ticks * 0.1;
                    Location p = center.clone().add(Math.cos(angle) * radius, 1 + Math.sin(ticks * 0.1), Math.sin(angle) * radius);
                    p.getWorld().spawnParticle(Particle.PORTAL, p, 3, 0.2, 0.3, 0.2, 0.02);
                    p.getWorld().spawnParticle(Particle.WITCH, p, 1, 0.1, 0.2, 0.1, 0);
                }

                if (ticks % 15 == 0) {
                    for (Entity e : center.getWorld().getNearbyEntities(center, radius, 4, radius)) {
                        if (e instanceof Player p && p != player) {
                            p.damage(2.5, player);
                            // Random chaos
                            if (random.nextBoolean()) {
                                p.setVelocity(p.getVelocity().add(new org.bukkit.util.Vector(
                                        (random.nextDouble() - 0.5) * 1.2,
                                        0.4,
                                        (random.nextDouble() - 0.5) * 1.2
                                )));
                            }
                        }
                    }
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
