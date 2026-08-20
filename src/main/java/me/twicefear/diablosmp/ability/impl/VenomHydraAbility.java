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

public class VenomHydraAbility implements DiabloAbility {

    private final DiabloSMP plugin;

    public VenomHydraAbility(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public StoneType getStoneType() {
        return StoneType.VENOM_HYDRA;
    }

    @Override
    public boolean isSecondary() {
        return false;
    }

    @Override
    public void execute(Player player) {
        Location loc = player.getLocation();
        player.sendMessage(ChatColor.DARK_GREEN + "[Venom Hydra] " + ChatColor.GREEN + "CORROSIVE WAVE!");
        player.getWorld().playSound(loc, Sound.ENTITY_SLIME_SQUISH, 1.5f, 0.6f);

        Vector dir = loc.getDirection().setY(0).normalize();
        new BukkitRunnable() {
            int step = 0;

            @Override
            public void run() {
                step++;
                if (step > 10) {
                    cancel();
                    return;
                }

                Location center = loc.clone().add(dir.clone().multiply(step));
                center.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, center, 20, 1.2, 0.5, 1.2, 0.05);

                for (Entity e : center.getWorld().getNearbyEntities(center, 2.5, 2.0, 2.5)) {
                    if (e != player && e instanceof LivingEntity le) {
                        le.damage(8.0, player);
                        le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 140, 2));
                        le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 80, 1));
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public void executeSecondary(Player player) {
        Location loc = player.getLocation();
        player.sendMessage(ChatColor.DARK_GREEN + "[Venom Hydra] " + ChatColor.DARK_GREEN + "TOXIC MIASMA SWARM!");
        player.getWorld().playSound(loc, Sound.ENTITY_SPIDER_AMBIENT, 1.2f, 0.5f);

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                ticks++;
                if (ticks > 120) { // 6 seconds
                    cancel();
                    return;
                }

                Location current = player.getLocation().add(0, 1, 0);
                double radius = 5.0;

                for (int i = 0; i < 15; i++) {
                    double angle = Math.random() * Math.PI * 2;
                    double r = Math.random() * radius;
                    Location pLoc = current.clone().add(r * Math.cos(angle), (Math.random() - 0.5) * 2, r * Math.sin(angle));
                    pLoc.getWorld().spawnParticle(Particle.ENTITY_EFFECT, pLoc, 3, 0.1, 0.8, 0.1, 1.0);
                }

                for (Entity e : current.getWorld().getNearbyEntities(current, radius, radius, radius)) {
                    if (e != player && e instanceof LivingEntity le) {
                        le.damage(2.0, player);
                        le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 40, 1));
                        le.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 100, 0));
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }
}
