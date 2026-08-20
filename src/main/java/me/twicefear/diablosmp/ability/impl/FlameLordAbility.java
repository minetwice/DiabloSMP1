package me.twicefear.diablosmp.ability.impl;

import me.twicefear.diablosmp.DiabloSMP;
import me.twicefear.diablosmp.ability.DiabloAbility;
import me.twicefear.diablosmp.stone.StoneType;
import org.bukkit.ChatColor;
import org.bukkit.Color;
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
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.5f, 0.7f);
        player.playSound(player.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1.2f, 0.9f);

        Vector direction = player.getLocation().getDirection().normalize();
        Location startLoc = player.getEyeLocation();

        new BukkitRunnable() {
            int step = 0;
            final Location current = startLoc.clone();

            @Override
            public void run() {
                if (step >= 25) {
                    cancel();
                    return;
                }
                step++;
                current.add(direction.clone().multiply(1.5));

                // Triple-helix dragon fire spiral
                double angle = step * 0.6;
                double r = 1.5;
                for (int h = 0; h < 3; h++) {
                    double offsetAngle = angle + (h * Math.PI * 2 / 3);
                    double x = Math.cos(offsetAngle) * r;
                    double y = Math.sin(offsetAngle) * r;
                    double z = Math.sin(offsetAngle) * r;
                    Location pLoc = current.clone().add(x, y, z);

                    current.getWorld().spawnParticle(Particle.FLAME, pLoc, 12, 0.1, 0.1, 0.1, 0.05);
                    current.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, pLoc, 6, 0.1, 0.1, 0.1, 0.05);
                    current.getWorld().spawnParticle(
                            Particle.DUST, pLoc, 5, 0.05, 0.05, 0.05,
                            new Particle.DustOptions(Color.fromRGB(255, 60, 0), 1.5f)
                    );
                }

                current.getWorld().spawnParticle(Particle.LAVA, current, 10, 0.3, 0.3, 0.3);
                current.getWorld().spawnParticle(Particle.FLASH, current, 1);

                for (Entity e : current.getWorld().getNearbyEntities(current, 3.0, 3.0, 3.0)) {
                    if (e != player && e instanceof LivingEntity le) {
                        le.damage(14.0, player);
                        le.setFireTicks(120);
                        le.setVelocity(direction.clone().multiply(1.4).setY(0.5));
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public void executeSecondary(Player player) {
        // Secondary: Hellfire Supernova
        player.sendMessage(ChatColor.DARK_RED + "[Flame Lord] " + ChatColor.RED + "HELLFIRE SUPERNOVA!");
        Location center = player.getLocation();
        center.getWorld().playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 0.5f);
        center.getWorld().playSound(center, Sound.ITEM_TRIDENT_THUNDER, 1.5f, 0.8f);

        new BukkitRunnable() {
            int radius = 1;

            @Override
            public void run() {
                if (radius > 15) {
                    cancel();
                    return;
                }

                // Expanding double shockwave ring
                for (int i = 0; i < 360; i += 6) {
                    double rad = Math.toRadians(i);
                    double x = radius * Math.cos(rad);
                    double z = radius * Math.sin(rad);

                    Location pLoc1 = center.clone().add(x, 0.5, z);
                    Location pLoc2 = center.clone().add(x * 0.8, 1.2, z * 0.8);

                    center.getWorld().spawnParticle(Particle.FLAME, pLoc1, 4, 0.1, 0.1, 0.1, 0.03);
                    center.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, pLoc2, 3, 0.1, 0.1, 0.1, 0.03);
                    center.getWorld().spawnParticle(
                            Particle.DUST, pLoc1, 2, 0.05, 0.05, 0.05,
                            new Particle.DustOptions(Color.fromRGB(255, 100, 0), 1.8f)
                    );
                }

                center.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, center, 2);

                for (Entity e : center.getWorld().getNearbyEntities(center, radius, 4, radius)) {
                    if (e != player && e instanceof LivingEntity le) {
                        le.damage(18.0, player);
                        le.setFireTicks(200);
                        le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 120, 1));
                    }
                }

                radius++;
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }
}
