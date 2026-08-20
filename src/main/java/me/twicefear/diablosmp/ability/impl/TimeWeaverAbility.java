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

public class TimeWeaverAbility implements DiabloAbility {

    private final DiabloSMP plugin;

    public TimeWeaverAbility(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public StoneType getStoneType() {
        return StoneType.TIME_WEAVER;
    }

    @Override
    public boolean isSecondary() {
        return false;
    }

    @Override
    public void execute(Player player) {
        Location startLoc = player.getLocation();
        player.sendMessage(ChatColor.BLUE + "[Time Weaver] " + ChatColor.AQUA + "CHRONO REWIND DASH!");
        player.getWorld().playSound(startLoc, Sound.BLOCK_BEACON_POWER_SELECT, 1.2f, 1.8f);

        Vector dash = startLoc.getDirection().multiply(2.0);
        player.setVelocity(dash);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    player.teleport(startLoc);
                    player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + 6.0));
                    player.sendMessage(ChatColor.BLUE + "Chronos rewound your position and restored health!");
                    player.getWorld().playSound(startLoc, Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1.2f, 1.5f);
                    player.getWorld().spawnParticle(Particle.REVERSE_PORTAL, startLoc.add(0, 1, 0), 30, 0.5, 1.0, 0.5, 0.1);
                }
            }
        }.runTaskLater(plugin, 60L); // Rewind back after 3s
    }

    public void executeSecondary(Player player) {
        Location loc = player.getLocation();
        player.sendMessage(ChatColor.BLUE + "[Time Weaver] " + ChatColor.DARK_BLUE + "TIME STASIS ZONE!");
        player.getWorld().playSound(loc, Sound.BLOCK_CONDUIT_ACTIVATE, 1.5f, 0.5f);

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                ticks++;
                if (ticks > 100) { // 5 seconds
                    cancel();
                    return;
                }

                loc.getWorld().spawnParticle(Particle.END_ROD, loc.clone().add(0, 1, 0), 20, 3.5, 1.0, 3.5, 0.01);

                for (Entity e : loc.getWorld().getNearbyEntities(loc, 4.5, 3.0, 4.5)) {
                    if (e != player && e instanceof LivingEntity le) {
                        le.setVelocity(new Vector(0, 0, 0));
                        le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 10));
                        le.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 40, 10));
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
