package com.twicefear.diablosmp.manager;

import com.twicefear.diablosmp.stone.StoneType;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownManager {

    private final Map<UUID, long[]> cooldowns = new ConcurrentHashMap<>();
    private static final int PRIMARY = 0;
    private static final int SECONDARY = 1;

    public void setPrimary(UUID uuid, int seconds) {
        cooldowns.computeIfAbsent(uuid, k -> new long[]{0, 0})[PRIMARY] = System.currentTimeMillis() + (seconds * 1000L);
    }
    public void setSecondary(UUID uuid, int seconds) {
        cooldowns.computeIfAbsent(uuid, k -> new long[]{0, 0})[SECONDARY] = System.currentTimeMillis() + (seconds * 1000L);
    }
    public boolean isPrimaryReady(UUID uuid) {
        long[] c = cooldowns.get(uuid);
        return c == null || c[PRIMARY] <= System.currentTimeMillis();
    }
    public boolean isSecondaryReady(UUID uuid) {
        long[] c = cooldowns.get(uuid);
        return c == null || c[SECONDARY] <= System.currentTimeMillis();
    }
    public long primaryRemaining(UUID uuid) {
        long[] c = cooldowns.get(uuid);
        if (c == null) return 0;
        return Math.max(0, (c[PRIMARY] - System.currentTimeMillis()) / 1000);
    }
    public long secondaryRemaining(UUID uuid) {
        long[] c = cooldowns.get(uuid);
        if (c == null) return 0;
        return Math.max(0, (c[SECONDARY] - System.currentTimeMillis()) / 1000);
    }
    public double primaryFraction(UUID uuid, StoneType type) {
        long[] c = cooldowns.get(uuid);
        if (c == null) return 0;
        long end = c[PRIMARY]; long now = System.currentTimeMillis();
        if (end <= now) return 0;
        int total = DiabloCooldowns.primary(type);
        if (total <= 0) return 0;
        return Math.min(1.0, Math.max(0.0, (double)(end - now) / (total * 1000.0)));
    }
    public double secondaryFraction(UUID uuid, StoneType type) {
        long[] c = cooldowns.get(uuid);
        if (c == null) return 0;
        long end = c[SECONDARY]; long now = System.currentTimeMillis();
        if (end <= now) return 0;
        int total = DiabloCooldowns.secondary(type);
        if (total <= 0) return 0;
        return Math.min(1.0, Math.max(0.0, (double)(end - now) / (total * 1000.0)));
    }
    public void clear(UUID uuid) { cooldowns.remove(uuid); }
}
