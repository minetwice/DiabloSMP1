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

public class IronTitanAbility implements DiabloAbility {

    private final DiabloSMP plugin;

    public IronTitanAbility(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public StoneType getStoneType() {
        return StoneType.IRON_TITAN;
    }

    @Override
    public boolean isSecondary() {
        return false;
    }

    @Override
    public void execute(Player player) {
        Location loc = player.getLocation();
        player.sendMessage(ChatColor.DARK_AQUA + "[Iron Titan] " + ChatColor.AQUA + "TITAN SHIELD CHARGE!");
        player.getWorld().playSound(loc, Sound.ITEM_SHIELD_BLOCK, 1.5f, 0.5f);

        Vector charge = player.getLocation().getDirection().setY(0).normalize().multiply(1.8);
        player.setVelocity(charge);

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                ticks++;
                if (ticks > 15) {
                    cancel();
                    return;
                }

                player.getWorld().spawnParticle(Particle.CRIT, player.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5, 0.1);

                for (Entity e : player.getNearbyEntities(2.0, 2.0, 2.0)) {
                    if (e != player && e instanceof LivingEntity le) {
                        le.damage(10.0, player);
                        le.setVelocity(charge.clone().add(new Vector(0, 0.6, 0)));
                        le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 2));
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public void executeSecondary(Player player) {
        Location loc = player.getLocation();
        player.sendMessage(ChatColor.DARK_AQUA + "[Iron Titan] " + ChatColor.BLUE + "FORTRESS EARTH SHATTER!");
        player.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 0.6f);

        player.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, loc.add(0, 0.5, 0), 3);

        for (Entity e : player.getNearbyEntities(6.0, 4.0, 6.0)) {
            if (e != player && e instanceof LivingEntity le) {
                le.damage(14.0, player);
                le.setVelocity(new Vector(0, 0.9, 0));
                le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 3));
            }
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 200, 3));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 1));
    }
}
