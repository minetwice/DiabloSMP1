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

public class VoidWalkerAbility implements DiabloAbility {
    private final DiabloSMP plugin;

    public VoidWalkerAbility(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public StoneType getStoneType() {
        return StoneType.VOID_WALKER;
    }

    @Override
    public boolean isSecondary() {
        return false;
    }

    @Override
    public void execute(Player player) {
        // Primary: Dimensional Rip / Teleport Dash
        player.sendMessage(ChatColor.DARK_PURPLE + "[Void Walker] " + ChatColor.LIGHT_PURPLE + "DIMENSIONAL RIP!");
        Location start = player.getLocation();
        Vector dir = start.getDirection().normalize().multiply(12);
        Location target = start.clone().add(dir);

        start.getWorld().spawnParticle(Particle.PORTAL, start, 50, 0.5, 1.0, 0.5, 0.5);
        player.teleport(target);
        target.getWorld().spawnParticle(Particle.REVERSE_PORTAL, target, 50, 0.5, 1.0, 0.5, 0.5);
        player.playSound(target, Sound.ENTITY_ENDERMAN_TELEPORT, 1.2f, 0.8f);

        for (Entity e : target.getWorld().getNearbyEntities(target, 4, 4, 4)) {
            if (e != player && e instanceof LivingEntity le) {
                le.damage(10.0, player);
                le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0));
            }
        }
    }

    public void executeSecondary(Player player) {
        // Secondary: Singularity Event Horizon
        player.sendMessage(ChatColor.DARK_PURPLE + "[Void Walker] " + ChatColor.DARK_RED + "SINGULARITY EVENT!");
        Location center = player.getTargetBlockExact(20) != null ?
                player.getTargetBlockExact(20).getLocation().add(0, 1, 0) : player.getLocation().add(player.getLocation().getDirection().multiply(8));

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= 100) { // 5s
                    center.getWorld().createExplosion(center, 3.0f, false, false);
                    cancel();
                    return;
                }
                ticks++;

                center.getWorld().spawnParticle(Particle.SQUID_INK, center, 20, 0.5, 0.5, 0.5, 0.1);
                center.getWorld().spawnParticle(Particle.DRAGON_BREATH, center, 15, 1.0, 1.0, 1.0, 0.05);

                for (Entity e : center.getWorld().getNearbyEntities(center, 8, 8, 8)) {
                    if (e != player && e instanceof LivingEntity le) {
                        Vector pull = center.toVector().subtract(le.getLocation().toVector()).normalize().multiply(0.4);
                        le.setVelocity(pull);
                        le.damage(1.5, player);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
