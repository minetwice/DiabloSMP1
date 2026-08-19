package com.twicefear.diablosmp.managers;

import com.twicefear.diablosmp.DiabloSMP;
import com.twicefear.diablosmp.stones.StoneType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    private final DiabloSMP plugin;
    // UUID -> (StoneType -> (isPrimary -> endTime))
    private final Map<UUID, Map<StoneType, Map<Boolean, Long>>> cooldowns = new HashMap<>();

    public CooldownManager(DiabloSMP plugin) {
        this.plugin = plugin;
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

        // This will be replaced by resource pack unicode bars later
        String primaryBar = primaryRem > 0 ? "§c" + (primaryRem / 1000) + "s" : "§aReady";
        String secondaryBar = secondaryRem > 0 ? "§c" + (secondaryRem / 1000) + "s" : "§aReady";

        Component msg = Component.text("§e[Primary] ")
                .append(Component.text(primaryBar))
                .append(Component.text("  §8|  "))
                .append(Component.text("§e[Secondary] "))
                .append(Component.text(secondaryBar));

        player.sendActionBar(msg);
    }

    public void clear(Player player) {
        cooldowns.remove(player.getUniqueId());
    }
}
