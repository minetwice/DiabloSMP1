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

public class LightningOverlordAbility implements DiabloAbility {

    private final DiabloSMP plugin;

    public LightningOverlordAbility(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public StoneType getStoneType() {
        return StoneType.LIGHTNING_OVERLORD;
    }

    @Override
    public boolean isSecondary() {
        return false;
    }

    @Override
    public void execute(Player player) {
        // Primary: Zeus Bolt Strike
        player.sendMessage(ChatColor.YELLOW + "[Lightning Overlord] " + ChatColor.GOLD + "ZEUS BOLT STRIKE!");
        Location target = player.getTargetBlockExact(20) != null ?
                player.getTargetBlockExact(20).getLocation() : player.getLocation().add(player.getLocation().getDirection().multiply(10));

        target.getWorld().strikeLightning(target);
        target.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, target, 100, 1.5, 1.5, 1.5, 0.2);

        for (Entity e : target.getWorld().getNearbyEntities(target, 5, 5, 5)) {
            if (e != player && e instanceof LivingEntity le) {
                le.damage(12.0, player);
                le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 1));
            }
        }
    }

    public void executeSecondary(Player player) {
        // Secondary: Raijin Storm Aura
        player.sendMessage(ChatColor.GOLD + "[Lightning Overlord] " + ChatColor.YELLOW + "RAIJIN STORM AURA!");

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (!player.isOnline() || ticks >= 80) { // 4 seconds
                    cancel();
                    return;
                }
                ticks++;

                Location pLoc = player.getLocation().add(0, 1, 0);
                pLoc.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, pLoc, 25, 1.2, 1.2, 1.2, 0.1);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 2));

                if (ticks % 10 == 0) {
                    player.playSound(pLoc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.8f, 1.5f);
                    for (Entity e : player.getNearbyEntities(6, 6, 6)) {
                        if (e != player && e instanceof LivingEntity le) {
                            le.damage(3.0, player);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
