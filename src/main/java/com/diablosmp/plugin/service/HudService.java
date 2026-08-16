package com.diablosmp.plugin.service;

import com.diablosmp.plugin.DiabloSMP;
import com.diablosmp.plugin.config.StoneConfig;
import com.diablosmp.plugin.model.DiabloStoneType;
import com.diablosmp.plugin.model.PlayerData;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HudService {
    private final DiabloSMP plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final Key hudFontKey = Key.key("diablosmp", "diablo_hud");
    private BukkitTask hudTask;

    // Mapping Private Use Area Unicode characters for Custom Font HUD
    private static final String ICON_NO_STONE = "\uE000";
    private static final String BAR_FILLED = "\uE101";
    private static final String BAR_EMPTY = "\uE102";

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

        Component finalHud;

        if (activeStone == null) {
            Component emptyIcon = Component.text(ICON_NO_STONE).font(hudFontKey);
            Component noStoneLabel = miniMessage.deserialize(plugin.getConfig().getString("hud.no-stone-text", "<gray>No Diablo Stone Absorbed</gray>"));
            finalHud = Component.empty().append(emptyIcon).append(Component.space()).append(noStoneLabel);
        } else {
            StoneConfig stoneConfig = plugin.getConfigManager().getStoneConfig(activeStone);
            String stoneChar = getStoneFontChar(activeStone);
            Component stoneIconComponent = Component.text(stoneChar).font(hudFontKey);

            double remainingSeconds = cooldownService.getRemainingSeconds(player, activeStone);

            if (remainingSeconds <= 0.0) {
                Component readyLabel = miniMessage.deserialize(plugin.getConfig().getString("hud.ready-text", "<red>READY</red>"));
                finalHud = Component.empty().append(stoneIconComponent).append(Component.space()).append(readyLabel);
            } else {
                double totalCd = stoneConfig != null ? stoneConfig.getCooldownSeconds() : 20.0;
                double fraction = Math.max(0.0, Math.min(1.0, 1.0 - (remainingSeconds / totalCd)));
                int totalBars = 10;
                int filledBars = (int) Math.round(fraction * totalBars);

                StringBuilder barChars = new StringBuilder();
                for (int i = 0; i < totalBars; i++) {
                    if (i < filledBars) {
                        barChars.append(BAR_FILLED);
                    } else {
                        barChars.append(BAR_EMPTY);
                    }
                }

                Component cooldownBarComponent = Component.text(barChars.toString()).font(hudFontKey);
                Component secondsComponent = Component.text(String.format(" %.1fs", remainingSeconds), NamedTextColor.GRAY);

                finalHud = Component.empty()
                        .append(stoneIconComponent)
                        .append(Component.space())
                        .append(cooldownBarComponent)
                        .append(secondsComponent);
            }
        }

        // Send via Adventure API Action Bar to render above XP level
        player.sendActionBar(finalHud);
    }

    private String getStoneFontChar(DiabloStoneType type) {
        int ordinal = type.ordinal(); // 0 to 14
        char puaChar = (char) (0xE001 + ordinal);
        return String.valueOf(puaChar);
    }

    public void removePlayer(UUID uuid) {
        // No-op cleanup
    }
}
