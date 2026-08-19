package me.twicefear.diablosmp.user;

import me.twicefear.diablosmp.stone.StoneType;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserData {
    private final UUID playerUuid;
    private StoneType absorbedStone;
    private final Map<String, Long> cooldowns = new HashMap<>();
    private long lastShiftTime = 0;
    private int shiftCount = 0;
    private boolean isFirstJoinAnimationActive = false;

    public UserData(UUID playerUuid) {
        this.playerUuid = playerUuid;
        this.absorbedStone = null;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public StoneType getAbsorbedStone() {
        return absorbedStone;
    }

    public void setAbsorbedStone(StoneType stone) {
        this.absorbedStone = stone;
    }

    public boolean hasAbsorbedStone() {
        return absorbedStone != null;
    }

    public void setCooldown(String abilityId, int durationSeconds) {
        long expiry = System.currentTimeMillis() + (durationSeconds * 1000L);
        cooldowns.put(abilityId, expiry);
    }

    public boolean isCooldowned(String abilityId) {
        Long expiry = cooldowns.get(abilityId);
        if (expiry == null) return false;
        return System.currentTimeMillis() < expiry;
    }

    public long getRemainingCooldownMillis(String abilityId) {
        Long expiry = cooldowns.get(abilityId);
        if (expiry == null) return 0;
        long rem = expiry - System.currentTimeMillis();
        return Math.max(0, rem);
    }

    public double getRemainingCooldownSeconds(String abilityId) {
        return getRemainingCooldownMillis(abilityId) / 1000.0;
    }

    public long getLastShiftTime() {
        return lastShiftTime;
    }

    public void setLastShiftTime(long lastShiftTime) {
        this.lastShiftTime = lastShiftTime;
    }

    public int getShiftCount() {
        return shiftCount;
    }

    public void setShiftCount(int shiftCount) {
        this.shiftCount = shiftCount;
    }

    public void incrementShiftCount() {
        this.shiftCount++;
    }

    public void resetShiftCount() {
        this.shiftCount = 0;
    }

    public boolean isFirstJoinAnimationActive() {
        return isFirstJoinAnimationActive;
    }

    public void setFirstJoinAnimationActive(boolean active) {
        this.isFirstJoinAnimationActive = active;
    }
}
