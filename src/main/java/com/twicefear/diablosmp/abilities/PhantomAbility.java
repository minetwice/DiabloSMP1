package com.twicefear.diablosmp.abilities;

import com.twicefear.diablosmp.DiabloSMP;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class PhantomAbility {

    private final DiabloSMP plugin;

    public PhantomAbility(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    public void primary(Player player) {
        // Phase Shift - short invis + speed + pass through feel
        player.sendMessage("§7§lPhase Shift!");
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PHANTOM_FLAP, 1.5f, 1.2f);

        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 60, 1));

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= 60 || !player.isOnline()) {
                    cancel();
                    return;
                }
                player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, player.getLocation().add(0, 1, 0), 4, 0.2, 0.4, 0.2, 0.01);
                player.getWorld().spawnParticle(Particle.SMOKE, player.getLocation(), 3, 0.2, 0.1, 0.2, 0.01);
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public void secondary(Player player) {
        // Ghost Realm
        Location center = player.getLocation();
        player.sendMessage("§7§lGhost Realm!");

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= 140) {
                    cancel();
                    return;
                }

                double radius = 6;
                for (int i = 0; i < 14; i++) {
                    double angle = (Math.PI * 2 / 14) * i + ticks * 0.06;
                    Location p = center.clone().add(Math.cos(angle) * radius, 1.2, Math.sin(angle) * radius);
                    p.getWorld().spawnParticle(Particle.SOUL, p, 2, 0.1, 0.3, 0.1, 0.01);
                    p.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, p, 1, 0.05, 0.2, 0.05, 0);
                }

                for (Entity e : center.getWorld().getNearbyEntities(center, radius, 4, radius)) {
                    if (e instanceof Player p && p != player) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 0));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 30, 1));
                        if (ticks % 20 == 0) p.damage(2.0, player);
                    }
                }

                // Owner phase benefits
                if (ticks % 20 == 0) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 25, 0));
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
