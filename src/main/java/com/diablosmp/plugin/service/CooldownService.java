package com.diablosmp.plugin.service;

import com.diablosmp.plugin.DiabloSMP;
import com.diablosmp.plugin.config.StoneConfig;
import com.diablosmp.plugin.model.DiabloStoneType;
import com.diablosmp.plugin.model.PlayerData;
import org.bukkit.entity.Player;

public class CooldownService {
    private final DiabloSMP plugin;

    public CooldownService(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    public boolean isOnCooldown(Player player, DiabloStoneType type) {
        if (player.hasPermission("diablosmp.bypasscooldown")) {
            return false;
        }
        PlayerData data = plugin.getStorageService().getPlayerData(player.getUniqueId());
        long exp = data.getCooldownExpiration(type);
        return System.currentTimeMillis() < exp;
    }

    public long getRemainingMillis(Player player, DiabloStoneType type) {
        if (player.hasPermission("diablosmp.bypasscooldown")) {
            return 0L;
        }
        PlayerData data = plugin.getStorageService().getPlayerData(player.getUniqueId());
        long exp = data.getCooldownExpiration(type);
        long remaining = exp - System.currentTimeMillis();
        return Math.max(0L, remaining);
    }

    public double getRemainingSeconds(Player player, DiabloStoneType type) {
        return getRemainingMillis(player, type) / 1000.0;
    }

    public void startCooldown(Player player, DiabloStoneType type) {
        if (player.hasPermission("diablosmp.bypasscooldown")) {
            return;
        }
        StoneConfig stoneConfig = plugin.getConfigManager().getStoneConfig(type);
        double seconds = stoneConfig != null ? stoneConfig.getCooldownSeconds() : 20.0;
        setCooldown(player, type, seconds);
    }

    public void setCooldown(Player player, DiabloStoneType type, double seconds) {
        PlayerData data = plugin.getStorageService().getPlayerData(player.getUniqueId());
        long exp = System.currentTimeMillis() + (long)(seconds * 1000.0);
        data.setCooldown(type, exp);
        plugin.getStorageService().savePlayerData(player.getUniqueId(), true);
    }

    public void resetCooldown(Player player, DiabloStoneType type) {
        PlayerData data = plugin.getStorageService().getPlayerData(player.getUniqueId());
        data.resetCooldown(type);
        plugin.getStorageService().savePlayerData(player.getUniqueId(), true);
    }

    public void resetAllCooldowns(Player player) {
        PlayerData data = plugin.getStorageService().getPlayerData(player.getUniqueId());
        data.resetAllCooldowns();
        plugin.getStorageService().savePlayerData(player.getUniqueId(), true);
    }
}
