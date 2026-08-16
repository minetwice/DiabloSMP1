package com.diablosmp.plugin.service;

import com.diablosmp.plugin.DiabloSMP;
import com.diablosmp.plugin.config.StoneConfig;
import com.diablosmp.plugin.model.DiabloStoneType;
import com.diablosmp.plugin.model.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class HudService {
    private final DiabloSMP plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private BukkitTask hudTask;

    public HudService(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if (!plugin.getConfig().getBoolean("hud.enabled", true)) {
            return;
        }
        long intervalTicks = plugin.getConfig().getLong("hud.update-interval-ticks", 4L);
        this.hudTask = Bukkit.getScheduler().runTaskTimer(plugin, this::updateAllHuds, intervalTicks, intervalTicks);
    }

    public void stop() {
        if (hudTask != null) {
            hudTask.cancel();
            hudTask = null;
        }
    }

    public void updateAllHuds() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateHud(player);
        }
    }

    public void updateHud(Player player) {
        if (!plugin.getConfig().getBoolean("hud.enabled", true)) return;

        PlayerData data = plugin.getStorageService().getPlayerDataIfLoaded(player.getUniqueId());
        if (data == null || !data.isHudEnabled() || !data.isPluginEnabledForPlayer()) {
            return;
        }

        DiabloStoneType activeStone = data.getActiveStone();
        CooldownService cooldownService = plugin.getCooldownService();

        StringBuilder barBuilder = new StringBuilder();

        // 1. Cooldown / Active Stone status section
        if (activeStone == null) {
            String noStoneText = plugin.getConfig().getString("hud.no-stone-text", "<gray>No Diablo Stone</gray>");
            barBuilder.append(noStoneText);
        } else {
            StoneConfig stoneConfig = plugin.getConfigManager().getStoneConfig(activeStone);
            String stoneDisplayName = stoneConfig != null ? stoneConfig.getDisplayName() : activeStone.getDefaultDisplayName();
            double remainingSeconds = cooldownService.getRemainingSeconds(player, activeStone);

            if (remainingSeconds <= 0.0) {
                String readyText = plugin.getConfig().getString("hud.ready-text", "<red>READY</red>");
                barBuilder.append(stoneDisplayName).append(" ").append(readyText);
            } else {
                double totalCd = stoneConfig != null ? stoneConfig.getCooldownSeconds() : 20.0;
                double fraction = Math.max(0.0, Math.min(1.0, 1.0 - (remainingSeconds / totalCd)));
                int totalBars = 10;
                int filledBars = (int) Math.round(fraction * totalBars);

                StringBuilder progress = new StringBuilder("<color:#FF5555>");
                for (int i = 0; i < totalBars; i++) {
                    if (i == filledBars) {
                        progress.append("</color><color:#AAAAAA>");
                    }
                    progress.append("▰");
                }
                progress.append("</color>");

                String cdFormat = plugin.getConfig().getString("hud.cooldown-format", "<color:#FF5555>{stone}</color> {bar} {seconds}s");
                String formatted = cdFormat
                        .replace("{stone}", stoneDisplayName)
                        .replace("{bar}", progress.toString())
                        .replace("{seconds}", String.format("%.1f", remainingSeconds));
                barBuilder.append(formatted);
            }
        }

        // 2. Diablo Fragments section (15 Diablo Stone Slots above XP level)
        barBuilder.append("  <gray>|</gray> ");
        for (DiabloStoneType type : DiabloStoneType.values()) {
            boolean owned = data.hasStone(type);
            boolean isActive = (activeStone == type);
            boolean onCd = cooldownService.isOnCooldown(player, type);

            if (!owned) {
                barBuilder.append("<color:#555555>◇</color>");
            } else if (isActive) {
                if (onCd) {
                    barBuilder.append("<color:#FF5555>◆</color>");
                } else {
                    barBuilder.append("<color:#00FF00>◆</color>");
                }
            } else {
                if (onCd) {
                    barBuilder.append("<color:#880000>◆</color>");
                } else {
                    barBuilder.append("<color:").append(type.getColorHex()).append(">◆</color>");
                }
            }
        }

        String hudText = barBuilder.toString();
        Component component = miniMessage.deserialize(hudText);
        // Continuously send action bar so it stays permanently visible above XP level
        player.sendActionBar(component);
    }

    public void removePlayer(UUID uuid) {
        // Cleanup tracking if any
    }
}
