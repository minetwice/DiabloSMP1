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

public class BloodBerserkerAbility implements DiabloAbility {

    private final DiabloSMP plugin;

    public BloodBerserkerAbility(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public StoneType getStoneType() {
        return StoneType.BLOOD_BERSERKER;
    }

    @Override
    public boolean isSecondary() {
        return false;
    }

    @Override
    public void execute(Player player) {
        Location loc = player.getLocation();
        player.sendMessage(ChatColor.DARK_RED + "[Blood Berserker] " + ChatColor.RED + "VAMPIRIC BURST!");
        player.getWorld().playSound(loc, Sound.ENTITY_PLAYER_HURT_SWEET_BERRY_BUSH, 1.5f, 0.5f);

        player.getWorld().spawnParticle(Particle.BLOCK, loc.add(0, 1, 0), 40, 1.5, 1.5, 1.5, 0.1, org.bukkit.Material.REDSTONE_BLOCK.createBlockData());

        double totalDmg = 0;
        for (Entity e : player.getNearbyEntities(4.5, 3.0, 4.5)) {
            if (e != player && e instanceof LivingEntity le) {
                le.damage(10.0, player);
                totalDmg += 5.0;
            }
        }

        if (totalDmg > 0) {
            player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + totalDmg));
            player.sendMessage(ChatColor.RED + "Drained " + totalDmg + " HP from nearby enemies!");
        }
    }

    public void executeSecondary(Player player) {
        Location loc = player.getLocation();
        player.sendMessage(ChatColor.DARK_RED + "[Blood Berserker] " + ChatColor.DARK_RED + "BLOOD AWAKENING!");
        player.getWorld().playSound(loc, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 0.6f);

        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 240, 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 240, 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 240, 1));

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                ticks++;
                if (!player.isOnline() || ticks > 120) {
                    cancel();
                    return;
                }

                player.getWorld().spawnParticle(Particle.DUST, player.getLocation().add(0, 1, 0), 8, 0.4, 0.8, 0.4, new Particle.DustOptions(org.bukkit.Color.RED, 1.5f));
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }
}
