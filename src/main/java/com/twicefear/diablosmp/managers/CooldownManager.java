package com.twicefear.diablosmp.managers;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.stones.StoneType;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    private final DiabloSMP plugin;
    // UUID -> (StoneType -> (isPrimary -> endTime))
    private final Map<UUID, Map<StoneType, Map<Boolean, Long>>> cooldowns = new HashMap<>();

    // Unicode from resource pack (when textures added)
    // \uE001 = empty bar, \uE002 = full bar, \uE010 = stone icon, \uE011 = question mark
    private static final String EMPTY_BAR = "\uE001";
    private static final String FULL_BAR = "\uE002";
    private static final String STONE_ICON = "\uE010";
    private static final String QUESTION = "\uE011";

    public CooldownManager(DiabloSMP plugin) {
        this.plugin = plugin;
        startActionBarTask();
    }

    private void startActionBarTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (plugin.getAbsorbManager().hasAbsorbed(player)) {
                        StoneType type = plugin.getAbsorbManager().getAbsorbed(player);
                        sendCooldownActionBar(player, type);
                    } else {
                        // Show question mark when no stone absorbed
                        player.sendActionBar(Component.text("§7" + QUESTION + " §8No Stone Absorbed"));
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 10L); // every 0.5 sec
    }

    public void startCooldown(Player player, StoneType type, boolean primary) {
        long duration = (primary ? type.getPrimaryCooldown() : type.getSecondaryCooldown()) * 1000L;
        cooldowns.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                .computeIfAbsent(type, k -> new HashMap<>())
                .put(primary, System.currentTimeMillis() + duration);
    }

    public boolean isOnCooldown(Player player, StoneType type, boolean primary) {
        Map<StoneType, Map<Boolean, Long>> playerMap = cooldowns.get(player.getUniqueId());
        if (playerMap == null) return false;
        Map<Boolean, Long> typeMap = playerMap.get(type);
        if (typeMap == null) return false;
        Long end = typeMap.get(primary);
        return end != null && end > System.currentTimeMillis();
    }

    public long getRemaining(Player player, StoneType type, boolean primary) {
        Map<StoneType, Map<Boolean, Long>> playerMap = cooldowns.get(player.getUniqueId());
        if (playerMap == null) return 0;
        Map<Boolean, Long> typeMap = playerMap.get(type);
        if (typeMap == null) return 0;
        Long end = typeMap.get(primary);
        if (end == null) return 0;
        return Math.max(0, end - System.currentTimeMillis());
    }

    public void sendCooldownActionBar(Player player, StoneType type) {
        long primaryRem = getRemaining(player, type, true);
        long secondaryRem = getRemaining(player, type, false);

        // Build bars (will look perfect once resource pack fonts are added)
        String primaryPart;
        String secondaryPart;

        if (primaryRem > 0) {
            primaryPart = "§c" + EMPTY_BAR + " " + (primaryRem / 1000) + "s";
        } else {
            primaryPart = "§a" + FULL_BAR + " Ready";
        }

        if (secondaryRem > 0) {
            secondaryPart = "§c" + EMPTY_BAR + " " + (secondaryRem / 1000) + "s";
        } else {
            secondaryPart = "§a" + FULL_BAR + " Ready";
        }

        // Format: [Primary Bar]  [Stone Icon]  [Secondary Bar]
        Component msg = Component.text(primaryPart + "  §f" + STONE_ICON + "  " + secondaryPart);
        player.sendActionBar(msg);
    }

    public void clear(Player player) {
        cooldowns.remove(player.getUniqueId());
    }
}
