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

public class WindTempestAbility implements DiabloAbility {

    private final DiabloSMP plugin;

    public WindTempestAbility(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public StoneType getStoneType() {
        return StoneType.WIND_TEMPEST;
    }

    @Override
    public boolean isSecondary() {
        return false;
    }

    @Override
    public void execute(Player player) {
        Location loc = player.getLocation();
        player.sendMessage(ChatColor.WHITE + "[Wind Tempest] " + ChatColor.AQUA + "GALE TORNADO VORTEX!");
        player.getWorld().playSound(loc, Sound.ITEM_ELYTRA_FLYING, 1.5f, 0.7f);

        Vector dir = loc.getDirection().setY(0).normalize();
        Location vortexCenter = loc.clone().add(dir.multiply(4));

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                ticks++;
                if (ticks > 80) { // 4 seconds
                    cancel();
                    return;
                }

                for (double height = 0; height <= 6.0; height += 0.5) {
                    double radius = 0.5 + (height * 0.4);
                    double angle = ticks * 0.3 + height;
                    double x = radius * Math.cos(angle);
                    double z = radius * Math.sin(angle);
                    vortexCenter.getWorld().spawnParticle(Particle.CLOUD, vortexCenter.clone().add(x, height, z), 2, 0.05, 0.05, 0.05, 0.02);
                }

                for (Entity e : vortexCenter.getWorld().getNearbyEntities(vortexCenter, 4.0, 6.0, 4.0)) {
                    if (e != player && e instanceof LivingEntity le) {
                        Vector pull = vortexCenter.toVector().subtract(le.getLocation().toVector()).normalize().multiply(0.4).setY(0.35);
                        le.setVelocity(pull);
                        le.damage(1.5, player);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public void executeSecondary(Player player) {
        Location loc = player.getLocation();
        player.sendMessage(ChatColor.WHITE + "[Wind Tempest] " + ChatColor.WHITE + "SKY BLADE DASH!");
        player.getWorld().playSound(loc, Sound.ENTITY_BAT_TAKEOFF, 1.2f, 1.5f);

        Vector dashDir = player.getLocation().getDirection().normalize().multiply(2.5).setY(0.4);
        player.setVelocity(dashDir);

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                ticks++;
                if (ticks > 15) {
                    cancel();
                    return;
                }

                player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, player.getLocation().add(0, 1, 0), 5, 0.5, 0.5, 0.5, 0.1);
                for (Entity e : player.getNearbyEntities(2.0, 2.0, 2.0)) {
                    if (e != player && e instanceof LivingEntity le) {
                        le.damage(10.0, player);
                        le.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 30, 2));
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
