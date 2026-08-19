package com.twicefear.diablosmp.abilities;

import com.twicefear.diablosmp.DiabloSMP;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class NatureAbility {

    private final DiabloSMP plugin;

    public NatureAbility(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    public void primary(Player player) {
        // Vine Trap + Thorns
        Location target = player.getTargetBlockExact(20) != null
                ? player.getTargetBlockExact(20).getLocation()
                : player.getLocation().add(player.getLocation().getDirection().multiply(10));

        player.sendMessage("§a§lVines erupt!");
        player.getWorld().playSound(target, Sound.BLOCK_GRASS_BREAK, 1.5f, 0.6f);

        List<Block> placed = new ArrayList<>();

        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                Block b = target.clone().add(x, 0, z).getBlock();
                if (b.getType().isAir()) {
                    b.setType(Material.OAK_LEAVES);
                    placed.add(b);
                }
            }
        }

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= 80) {
                    for (Block b : placed) {
                        if (b.getType() == Material.OAK_LEAVES) b.setType(Material.AIR);
                    }
                    cancel();
                    return;
                }

                for (Entity e : target.getWorld().getNearbyEntities(target, 3, 3, 3)) {
                    if (e instanceof Player p && p != player) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 30, 2));
                        p.damage(1.5, player);
                        p.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, p.getLocation().add(0, 1, 0), 5, 0.3, 0.5, 0.3, 0.02);
                    }
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public void secondary(Player player) {
        // Jungle Domain
        Location center = player.getLocation();
        player.sendMessage("§a§lJungle Domain!");

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= 160) {
                    cancel();
                    return;
                }

                double radius = 7;
                for (int i = 0; i < 16; i++) {
                    double angle = (Math.PI * 2 / 16) * i + ticks * 0.03;
                    Location p = center.clone().add(Math.cos(angle) * radius, 0.5, Math.sin(angle) * radius);
                    p.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, p, 2, 0.2, 0.4, 0.2, 0.01);
                    p.getWorld().spawnParticle(Particle.COMPOSTER, p, 1, 0.1, 0.2, 0.1, 0);
                }

                for (Entity e : center.getWorld().getNearbyEntities(center, radius, 4, radius)) {
                    if (e instanceof Player p && p != player) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 40, 0));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 30, 1));
                        if (ticks % 25 == 0) p.damage(2.0, player);
                    } else if (e instanceof Player p && p == player) {
                        if (ticks % 20 == 0) {
                            p.setHealth(Math.min(p.getMaxHealth(), p.getHealth() + 1));
                            p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 0));
                        }
                    }
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
