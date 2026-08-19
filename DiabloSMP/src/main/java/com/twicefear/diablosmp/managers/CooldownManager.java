package com.twicefear.diablosmp.managers;

import com.twicefear.diablosmp.DiabloSMPPlugin;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.*;

public class CooldownManager {
    
    private final DiabloSMPPlugin plugin;
    private final Map<UUID, Map<String, Long>> playerCooldowns = new HashMap<>();
    private final Map<UUID, BossBar> primaryCooldownBars = new HashMap<>();
    private final Map<UUID, BossBar> secondaryCooldownBars = new HashMap<>();
    
    public CooldownManager(DiabloSMPPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void setCooldown(UUID playerUuid, String abilityKey, long cooldownMs, boolean isPrimary) {
        playerCooldowns.computeIfAbsent(playerUuid, k -> new HashMap<>())
                .put(abilityKey + (isPrimary ? "_primary" : "_secondary"), System.currentTimeMillis() + cooldownMs);
    }
    
    public boolean isOnCooldown(UUID playerUuid, String abilityKey, boolean isPrimary) {
        Map<String, Long> cooldowns = playerCooldowns.get(playerUuid);
        if (cooldowns == null) return false;
        
        Long endTime = cooldowns.get(abilityKey + (isPrimary ? "_primary" : "_secondary"));
        return endTime != null && System.currentTimeMillis() < endTime;
    }
    
    public long getRemainingCooldown(UUID playerUuid, String abilityKey, boolean isPrimary) {
        Map<String, Long> cooldowns = playerCooldowns.get(playerUuid);
        if (cooldowns == null) return 0;
        
        Long endTime = cooldowns.get(abilityKey + (isPrimary ? "_primary" : "_secondary"));
        if (endTime == null) return 0;
        
        long remaining = endTime - System.currentTimeMillis();
        return Math.max(0, remaining);
    }
    
    public void updateCooldownDisplay(Player player, String stoneType, long primaryRemaining, long secondaryRemaining, 
                                      int primaryTotal, int secondaryTotal) {
        // Update boss bars for visual cooldown display
        double primaryPercent = 1.0 - ((double) primaryRemaining / primaryTotal);
        double secondaryPercent = 1.0 - ((double) secondaryRemaining / secondaryTotal);
        
        BossBar primaryBar = primaryCooldownBars.computeIfAbsent(player.getUniqueId(), uuid -> {
            BossBar bar = Bukkit.createBossBar("", BarColor.RED, BarStyle.SOLID);
            bar.addPlayer(player);
            return bar;
        });
        
        BossBar secondaryBar = secondaryCooldownBars.computeIfAbsent(player.getUniqueId(), uuid -> {
            BossBar bar = Bukkit.createBossBar("", BarColor.BLUE, BarStyle.SOLID);
            bar.addPlayer(player);
            return bar;
        });
        
        primaryBar.setTitle("§c⚔ Primary: " + formatTime(primaryRemaining));
        primaryBar.setProgress(Math.max(0, Math.min(1, primaryPercent)));
        
        secondaryBar.setTitle("§b🛡 Secondary: " + formatTime(secondaryRemaining));
        secondaryBar.setProgress(Math.max(0, Math.min(1, secondaryPercent)));
    }
    
    public void clearCooldowns(UUID playerUuid) {
        playerCooldowns.remove(playerUuid);
        
        BossBar primaryBar = primaryCooldownBars.remove(playerUuid);
        if (primaryBar != null) primaryBar.removeAll();
        
        BossBar secondaryBar = secondaryCooldownBars.remove(playerUuid);
        if (secondaryBar != null) secondaryBar.removeAll();
    }
    
    private String formatTime(long ms) {
        long seconds = ms / 1000;
        if (seconds >= 60) {
            return (seconds / 60) + "m " + (seconds % 60) + "s";
        }
        return seconds + "s";
    }
}
