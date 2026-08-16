package com.diablosmp.plugin.model;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerData {
    private final UUID uuid;
    private final Set<DiabloStoneType> ownedStones;
    private DiabloStoneType activeStone;
    private final Map<DiabloStoneType, Long> cooldowns;
    private boolean firstJoinClaimed;
    private boolean hudEnabled;
    private boolean pluginEnabledForPlayer;
    private long lastCastTimestamp;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.ownedStones = Collections.synchronizedSet(EnumSet.noneOf(DiabloStoneType.class));
        this.activeStone = null;
        this.cooldowns = new ConcurrentHashMap<>();
        this.firstJoinClaimed = false;
        this.hudEnabled = true;
        this.pluginEnabledForPlayer = true;
        this.lastCastTimestamp = 0L;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Set<DiabloStoneType> getOwnedStones() {
        return ownedStones;
    }

    public boolean hasStone(DiabloStoneType type) {
        return ownedStones.contains(type);
    }

    public void addStone(DiabloStoneType type) {
        ownedStones.add(type);
        if (activeStone == null) {
            activeStone = type;
        }
    }

    public void removeStone(DiabloStoneType type) {
        ownedStones.remove(type);
        if (activeStone == type) {
            activeStone = ownedStones.isEmpty() ? null : ownedStones.iterator().next();
        }
    }

    public DiabloStoneType getActiveStone() {
        if ((activeStone == null || !ownedStones.contains(activeStone)) && !ownedStones.isEmpty()) {
            activeStone = ownedStones.iterator().next();
        }
        return activeStone;
    }

    public void setActiveStone(DiabloStoneType activeStone) {
        if (activeStone == null || ownedStones.contains(activeStone)) {
            this.activeStone = activeStone;
        }
    }

    public Map<DiabloStoneType, Long> getCooldowns() {
        return cooldowns;
    }

    public long getCooldownExpiration(DiabloStoneType type) {
        return cooldowns.getOrDefault(type, 0L);
    }

    public void setCooldown(DiabloStoneType type, long expirationTimestampMs) {
        if (expirationTimestampMs <= System.currentTimeMillis()) {
            cooldowns.remove(type);
        } else {
            cooldowns.put(type, expirationTimestampMs);
        }
    }

    public void resetCooldown(DiabloStoneType type) {
        cooldowns.remove(type);
    }

    public void resetAllCooldowns() {
        cooldowns.clear();
    }

    public boolean isFirstJoinClaimed() {
        return firstJoinClaimed;
    }

    public void setFirstJoinClaimed(boolean firstJoinClaimed) {
        this.firstJoinClaimed = firstJoinClaimed;
    }

    public boolean isHudEnabled() {
        return hudEnabled;
    }

    public void setHudEnabled(boolean hudEnabled) {
        this.hudEnabled = hudEnabled;
    }

    public boolean isPluginEnabledForPlayer() {
        return pluginEnabledForPlayer;
    }

    public void setPluginEnabledForPlayer(boolean pluginEnabledForPlayer) {
        this.pluginEnabledForPlayer = pluginEnabledForPlayer;
    }

    public long getLastCastTimestamp() {
        return lastCastTimestamp;
    }

    public void setLastCastTimestamp(long lastCastTimestamp) {
        this.lastCastTimestamp = lastCastTimestamp;
    }
}
