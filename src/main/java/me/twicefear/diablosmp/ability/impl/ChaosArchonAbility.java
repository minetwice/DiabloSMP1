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

public class ChaosArchonAbility implements DiabloAbility {

    private final DiabloSMP plugin;

    public ChaosArchonAbility(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public StoneType getStoneType() {
        return StoneType.CHAOS_ARCHON;
    }

    @Override
    public boolean isSecondary() {
        return false;
    }

    @Override
    public void execute(Player player) {
        Location loc = player.getLocation();
        player.sendMessage(ChatColor.LIGHT_PURPLE + "[Chaos Archon] " + ChatColor.RED + "METAMORPHOSIS BLAST!");
        player.getWorld().playSound(loc, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1.5f, 0.8f);

        player.getWorld().spawnParticle(Particle.DRAGON_BREATH, loc.add(0, 1, 0), 50, 2.0, 1.0, 2.0, 0.1);

        for (Entity e : player.getNearbyEntities(5.0, 4.0, 5.0)) {
            if (e != player && e instanceof LivingEntity le) {
                le.damage(12.0, player);
                le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 1));
                le.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 40, 2));
            }
        }
    }

    public void executeSecondary(Player player) {
        Location loc = player.getTargetBlockExact(15) != null ?
                player.getTargetBlockExact(15).getLocation() : player.getLocation().add(player.getLocation().getDirection().multiply(8));

        Location skyLoc = loc.clone().add(0, 20, 0);
        player.sendMessage(ChatColor.LIGHT_PURPLE + "[Chaos Archon] " + ChatColor.DARK_RED + "CATACLYSMIC METEOR!");
        player.getWorld().playSound(loc, Sound.ENTITY_WITHER_SPAWN, 1.2f, 0.5f);

        new BukkitRunnable() {
            Location current = skyLoc.clone();

            @Override
            public void run() {
                current.subtract(0, 1.5, 0);
                current.getWorld().spawnParticle(Particle.FLAME, current, 30, 1.0, 1.0, 1.0, 0.1);
                current.getWorld().spawnParticle(Particle.LAVA, current, 10, 0.5, 0.5, 0.5, 0.05);

                if (current.getY() <= loc.getY()) {
                    current.getWorld().createExplosion(loc, 5.0f, true, false);
                    current.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 0.5f);

                    for (Entity e : loc.getWorld().getNearbyEntities(loc, 6.0, 5.0, 6.0)) {
                        if (e != player && e instanceof LivingEntity le) {
                            le.damage(22.0, player);
                            le.setFireTicks(160);
                        }
                    }
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
