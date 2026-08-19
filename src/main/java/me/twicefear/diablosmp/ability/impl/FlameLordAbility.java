package me.twicefear.diablosmp.ability.impl;

import me.twicefear.diablosmp.DiabloSMP;
import me.twicefear.diablosmp.ability.DiabloAbility;
import me.twicefear.diablosmp.stone.StoneType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class FlameLordAbility implements DiabloAbility {

    private final DiabloSMP plugin;

    public FlameLordAbility(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public StoneType getStoneType() {
        return StoneType.FLAME_LORD;
    }

    @Override
    public boolean isSecondary() {
        return false;
    }

    @Override
    public void execute(Player player) {
        // Primary: Infernal Dragon Strike
        player.sendMessage(ChatColor.RED + "[Flame Lord] " + ChatColor.GOLD + "INFERNAL DRAGON STRIKE!");
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.2f, 0.8f);

        Vector direction = player.getLocation().getDirection().normalize();
        Location startLoc = player.getEyeLocation();

        new BukkitRunnable() {
            int step = 0;
            final Location current = startLoc.clone();

            @Override
            public void run() {
                if (step >= 20) {
                    cancel();
                    return;
                }
                step++;
                current.add(direction.clone().multiply(1.5));

                // Dragon Spiral Particles
                double angle = step * 0.5;
                double r = 1.2;
                Location p1 = current.clone().add(Math.cos(angle) * r, Math.sin(angle) * r, Math.sin(angle) * r);
                Location p2 = current.clone().add(-Math.cos(angle) * r, -Math.sin(angle) * r, -Math.sin(angle) * r);

                current.getWorld().spawnParticle(Particle.FLAME, p1, 10, 0.1, 0.1, 0.1, 0.05);
                current.getWorld().spawnParticle(Particle.DRAGON_BREATH, p2, 10, 0.1, 0.1, 0.1, 0.05);
                current.getWorld().spawnParticle(Particle.LAVA, current, 5, 0.2, 0.2, 0.2);

                for (Entity e : current.getWorld().getNearbyEntities(current, 2.5, 2.5, 2.5)) {
                    if (e != player && e instanceof LivingEntity le) {
                        le.damage(12.0, player);
                        le.setFireTicks(100);
                        le.setVelocity(direction.clone().multiply(1.2).setY(0.4));
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public void executeSecondary(Player player) {
        // Secondary: Hellfire Supernova
        player.sendMessage(ChatColor.DARK_RED + "[Flame Lord] " + ChatColor.RED + "HELLFIRE SUPERNOVA!");
        Location center = player.getLocation();
        center.getWorld().playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 0.5f);

        new BukkitRunnable() {
            int radius = 1;

            @Override
            public void run() {
                if (radius > 12) {
                    cancel();
                    return;
                }

                for (int i = 0; i < 360; i += 10) {
                    double rad = Math.toRadians(i);
                    double x = radius * Math.cos(rad);
                    double z = radius * Math.sin(rad);
                    Location pLoc = center.clone().add(x, 0.5, z);

                    center.getWorld().spawnParticle(Particle.FLAME, pLoc, 3, 0.1, 0.1, 0.1, 0.02);
                    center.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, pLoc, 2, 0.1, 0.1, 0.1, 0.02);
                }

                for (Entity e : center.getWorld().getNearbyEntities(center, radius, 3, radius)) {
                    if (e != player && e instanceof LivingEntity le) {
                        le.damage(16.0, player);
                        le.setFireTicks(160);
                        le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 1));
                    }
                }

                radius++;
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }
}
