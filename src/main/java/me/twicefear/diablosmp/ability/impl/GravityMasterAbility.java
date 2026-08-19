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

public class GravityMasterAbility implements DiabloAbility {

    private final DiabloSMP plugin;

    public GravityMasterAbility(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public StoneType getStoneType() {
        return StoneType.GRAVITY_MASTER;
    }

    @Override
    public boolean isSecondary() {
        return false;
    }

    @Override
    public void execute(Player player) {
        Location loc = player.getLocation();
        player.sendMessage(ChatColor.LIGHT_PURPLE + "[Gravity Master] " + ChatColor.DARK_PURPLE + "GRAVITATIONAL CRUSH!");
        player.getWorld().playSound(loc, Sound.BLOCK_ANVIL_FALL, 1.2f, 0.5f);

        Location target = player.getTargetBlockExact(12) != null ? player.getTargetBlockExact(12).getLocation() : loc.clone().add(loc.getDirection().multiply(6));

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                ticks++;
                if (ticks > 60) { // 3 seconds
                    cancel();
                    return;
                }

                target.getWorld().spawnParticle(Particle.PORTAL, target.clone().add(0, 1, 0), 25, 2.0, 1.0, 2.0, 0.1);

                for (Entity e : target.getWorld().getNearbyEntities(target, 5.0, 4.0, 5.0)) {
                    if (e != player && e instanceof LivingEntity le) {
                        le.setVelocity(new Vector(0, -1.2, 0));
                        le.damage(2.0, player);
                        le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 3));
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public void executeSecondary(Player player) {
        Location loc = player.getLocation();
        player.sendMessage(ChatColor.LIGHT_PURPLE + "[Gravity Master] " + ChatColor.LIGHT_PURPLE + "ANTI-GRAVITY BURST!");
        player.getWorld().playSound(loc, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.5f, 1.2f);

        for (Entity e : player.getNearbyEntities(6.0, 4.0, 6.0)) {
            if (e != player && e instanceof LivingEntity le) {
                le.setVelocity(new Vector(0, 1.8, 0));
                le.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 60, 3));
            }
        }

        player.setVelocity(new Vector(0, 1.5, 0));
        player.getWorld().spawnParticle(Particle.REVERSE_PORTAL, loc.add(0, 1, 0), 50, 2.0, 1.0, 2.0, 0.2);
    }
}
