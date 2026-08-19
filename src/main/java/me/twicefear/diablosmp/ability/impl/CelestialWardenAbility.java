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

public class CelestialWardenAbility implements DiabloAbility {

    private final DiabloSMP plugin;

    public CelestialWardenAbility(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public StoneType getStoneType() {
        return StoneType.CELESTIAL_WARDEN;
    }

    @Override
    public boolean isSecondary() {
        return false;
    }

    @Override
    public void execute(Player player) {
        Location loc = player.getEyeLocation();
        player.sendMessage(ChatColor.YELLOW + "[Celestial Warden] " + ChatColor.GOLD + "SOLAR BEAM CANNON!");
        player.getWorld().playSound(loc, Sound.BLOCK_BEACON_POWER_SELECT, 1.5f, 1.2f);

        Vector dir = loc.getDirection().normalize();

        new BukkitRunnable() {
            int distance = 0;

            @Override
            public void run() {
                distance += 2;
                if (distance > 30) {
                    cancel();
                    return;
                }

                Location beamLoc = loc.clone().add(dir.clone().multiply(distance));
                beamLoc.getWorld().spawnParticle(Particle.END_ROD, beamLoc, 15, 0.4, 0.4, 0.4, 0.05);
                beamLoc.getWorld().spawnParticle(Particle.FLASH, beamLoc, 1, 0, 0, 0, 0);

                for (Entity e : beamLoc.getWorld().getNearbyEntities(beamLoc, 2.0, 2.0, 2.0)) {
                    if (e != player && e instanceof LivingEntity le) {
                        le.damage(16.0, player);
                        le.setFireTicks(100);
                        le.setVelocity(dir.clone().multiply(1.5).setY(0.5));
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public void executeSecondary(Player player) {
        Location loc = player.getLocation();
        player.sendMessage(ChatColor.YELLOW + "[Celestial Warden] " + ChatColor.WHITE + "SANCTUARY SHIELD!");
        player.getWorld().playSound(loc, Sound.ITEM_SHIELD_BLOCK, 1.5f, 0.8f);

        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 300, 4));
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 300, 2));

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                ticks++;
                if (!player.isOnline() || ticks > 100) { // 5 seconds shield ring
                    cancel();
                    return;
                }

                Location pLoc = player.getLocation().add(0, 1, 0);
                for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 8) {
                    double x = 2.5 * Math.cos(angle);
                    double z = 2.5 * Math.sin(angle);
                    pLoc.getWorld().spawnParticle(Particle.INSTANT_EFFECT, pLoc.clone().add(x, Math.sin(ticks * 0.2), z), 1, 0, 0, 0, 0);
                }

                for (Entity e : player.getNearbyEntities(2.5, 2.0, 2.5)) {
                    if (e != player && e instanceof LivingEntity le) {
                        Vector knockback = le.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(1.2).setY(0.4);
                        le.setVelocity(knockback);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
