package com.twicefear.diablosmp.managers;

import com.twicefear.diablosmp.DiabloSMPPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class ParticleManager {
    
    private final DiabloSMPPlugin plugin;
    
    public ParticleManager(DiabloSMPPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Play complex join reward particle animation
     */
    public void playJoinRewardAnimation(Player player) {
        Location loc = player.getLocation();
        
        // Make player invulnerable during animation
        player.setInvulnerable(true);
        
        // Lift player up
        new BukkitRunnable() {
            int ticks = 0;
            final int maxTicks = 160; // 8 seconds
            
            @Override
            public void run() {
                if (!player.isOnline() || ticks >= maxTicks) {
                    player.setInvulnerable(false);
                    cancel();
                    return;
                }
                
                // Lift player up and down
                if (ticks < 40) {
                    player.teleport(loc.add(0, 0.15, 0));
                } else if (ticks > maxTicks - 40) {
                    player.teleport(loc.subtract(0, 0.15, 0));
                }
                
                // Complex spiral particles
                for (int i = 0; i < 8; i++) {
                    double angle = (ticks * 0.2) + (i * Math.PI / 4);
                    double radius = 1.5 + Math.sin(ticks * 0.1) * 0.5;
                    
                    double x = Math.cos(angle) * radius;
                    double z = Math.sin(angle) * radius;
                    
                    Location particleLoc = loc.clone().add(x, 1 + Math.abs(Math.sin(ticks * 0.15)), z);
                    
                    // Multiple particle types
                    player.getWorld().spawnParticle(Particle.SPELL_MOB, particleLoc, 5, 0.3, 0.3, 0.3, 1, null, true);
                    player.getWorld().spawnParticle(Particle.PORTAL, particleLoc, 3, 0.5, 0.5, 0.5, 0.1, null, true);
                    player.getWorld().spawnParticle(Particle.DRAGON_BREATH, particleLoc, 2, 0.4, 0.4, 0.4, 0.05, null, true);
                }
                
                // Ring particles expanding outward
                if (ticks % 10 == 0) {
                    double ringRadius = (ticks % 40) * 0.1;
                    for (int i = 0; i < 36; i++) {
                        double angle = i * Math.PI / 18;
                        double x = Math.cos(angle) * ringRadius;
                        double z = Math.sin(angle) * ringRadius;
                        Location ringLoc = loc.clone().add(x, 0.5, z);
                        player.getWorld().spawnParticle(Particle.END_ROD, ringLoc, 1, 0, 0, 0, 0, null, true);
                    }
                }
                
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    
    /**
     * Play stone absorption animation
     */
    public void playAbsorbAnimation(Player player, Location center) {
        new BukkitRunnable() {
            int ticks = 0;
            final int maxTicks = 80;
            
            @Override
            public void run() {
                if (!player.isOnline() || ticks >= maxTicks) {
                    cancel();
                    return;
                }
                
                // Spiral particles going into player
                for (int i = 0; i < 4; i++) {
                    double angle = (ticks * 0.3) + (i * Math.PI / 2);
                    double radius = 2.0 - (ticks * 0.025);
                    
                    if (radius < 0.3) radius = 0.3;
                    
                    double x = Math.cos(angle) * radius;
                    double z = Math.sin(angle) * radius;
                    double y = 1.0 + Math.sin(ticks * 0.2) * 0.5;
                    
                    Location particleLoc = center.clone().add(x, y, z);
                    player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, particleLoc, 2, 0.1, 0.1, 0.1, 0.1, null, true);
                    player.getWorld().spawnParticle(Particle.ENCHANT, particleLoc, 1, 0.2, 0.2, 0.2, 0.5, null, true);
                }
                
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    
    /**
     * Show all stones popup during join animation
     */
    public void showStonesPopup(Player player) {
        // This will be handled by UI manager with custom packets
        // For now, send title messages showing stones
        List<String> stoneNames = Arrays.asList(
            "§4§lEARTHQUAKE", "§c§lINFERNO", "§b§lFROSTBITE",
            "§7§lTEMPEST", "§5§lVOID", "§e§lLIGHTNING",
            "§d§lCELESTIAL", "§8§lABYSSAL", "§6§lSOLAR",
            "§f§lLUNAR", "§3§lCHRONO", "§9§lSPECTRAL",
            "§2§lPRIMAL", "§5§lCOSMIC", "§b§lETHEREAL", "§7§lSHADOW"
        );
        
        new BukkitRunnable() {
            int index = 0;
            
            @Override
            public void run() {
                if (!player.isOnline() || index >= stoneNames.size()) {
                    cancel();
                    return;
                }
                
                player.sendTitle("§6§lDIABLO STONES", stoneNames.get(index), 5, 20, 5);
                index++;
            }
        }.runTaskTimer(plugin, 20L, 15L);
    }
}
