package com.twicefear.diablosmp.managers;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.stones.StoneType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class FirstJoinManager {

    private final DiabloSMP plugin;
    private final Set<UUID> protectedPlayers = new HashSet<>();
    private final Random random = new Random();

    public FirstJoinManager(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    public boolean isProtected(Player player) {
        return protectedPlayers.contains(player.getUniqueId());
    }

    public void startFirstJoinAnimation(Player player) {
        if (!plugin.getConfigManager().isFirstJoinEnabled()) return;

        protectedPlayers.add(player.getUniqueId());

        Location start = player.getLocation().clone();
        int duration = plugin.getConfigManager().getFirstJoinAnimationDuration();

        // Levitation + particles
        player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, duration * 20, 0, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, duration * 20, 0, false, false));

        new BukkitRunnable() {
            int ticks = 0;
            final int totalTicks = duration * 20;

            @Override
            public void run() {
                if (!player.isOnline() || ticks >= totalTicks) {
                    // End animation
                    player.removePotionEffect(PotionEffectType.LEVITATION);
                    player.removePotionEffect(PotionEffectType.GLOWING);
                    protectedPlayers.remove(player.getUniqueId());

                    // Give random stone
                    StoneType[] types = StoneType.values();
                    StoneType randomStone = types[random.nextInt(types.length)];
                    player.getInventory().addItem(plugin.getStoneManager().createStoneItem(randomStone));

                    player.sendMessage(plugin.getConfigManager().getPrefix() +
                            "§aYou received a legendary §e" + randomStone.getColoredName() + "§a!");
                    player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);

                    cancel();
                    return;
                }

                // Complex particle ring around player
                double radius = 2.5;
                for (int i = 0; i < 16; i++) {
                    double angle = (Math.PI * 2 / 16) * i + (ticks * 0.1);
                    double x = Math.cos(angle) * radius;
                    double z = Math.sin(angle) * radius;
                    Location loc = player.getLocation().add(x, 1.5 + Math.sin(ticks * 0.15) * 0.8, z);

                    // Cycle through all stone particles
                    StoneType show = StoneType.values()[ticks % StoneType.values().length];
                    player.getWorld().spawnParticle(show.getParticle(), loc, 3, 0.05, 0.05, 0.05, 0.01);
                }

                // Rising particles under player
                player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().add(0, 0.1, 0), 5, 0.3, 0.1, 0.3, 0.02);

                if (ticks % 10 == 0) {
                    player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 0.4f, 1.5f + (ticks * 0.01f));
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
