package com.sdmine.elementalcore.variants;

import com.sdmine.elementalcore.core.CoreType;
import java.util.HashMap;
import java.util.Map;

public class WeaponVariant {
    private final String id;
    private final String combinationKey;
    private final Map<CoreType, Integer> combination;
    private String displayName;
    private int customModelData;
    private Map<String, Object> passiveConfig;
    private String passiveDescription;
    private Map<String, Object> activeConfig;
    private String activeDescription;
    private Map<String, Object> particleConfig;
    private String sound;
    private int cooldown;

    public WeaponVariant(String id) {
        this.id = id;
        this.combination = new HashMap<>();
        this.combinationKey = "";
        this.passiveConfig = new HashMap<>();
        this.activeConfig = new HashMap<>();
        this.particleConfig = new HashMap<>();
        this.passiveDescription = "";
        this.activeDescription = "";
        this.cooldown = 0;
    }

    public boolean matches(CoreType[] sockets) {
        int[] counts = new int[CoreType.values().length];
        for (CoreType t : sockets) if (t != null) counts[t.ordinal()]++;
        for (Map.Entry<CoreType, Integer> entry : combination.entrySet())
            if (counts[entry.getKey().ordinal()] != entry.getValue()) return false;
        for (CoreType type : CoreType.values())
            if (counts[type.ordinal()] != combination.getOrDefault(type, 0)) return false;
        return true;
    }

    public String getId() { return id; }
    public String getCombinationKey() { return combinationKey; }
    public void setCombinationKey(String key) { this.combinationKey = key; }
    public Map<CoreType, Integer> getCombination() { return combination; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String n) { this.displayName = n; }
    public int getCustomModelData() { return customModelData; }
    public void setCustomModelData(int c) { this.customModelData = c; }
    public Map<String, Object> getPassiveConfig() { return passiveConfig; }
    public void setPassiveConfig(Map<String, Object> p) { this.passiveConfig = p; }
    public String getPassiveDescription() { return passiveDescription; }
    public void setPassiveDescription(String p) { this.passiveDescription = p; }
    public Map<String, Object> getActiveConfig() { return activeConfig; }
    public void setActiveConfig(Map<String, Object> a) { this.activeConfig = a; }
    public String getActiveDescription() { return activeDescription; }
    public void setActiveDescription(String a) { this.activeDescription = a; }
    public Map<String, Object> getParticleConfig() { return particleConfig; }
    public void setParticleConfig(Map<String, Object> p) { this.particleConfig = p; }
    public String getSound() { return sound; }
    public void setSound(String s) { this.sound = s; }
    public int getCooldown() { return cooldown; }
    public void setCooldown(int c) { this.cooldown = c; }
    public String getPassiveType() { return String.valueOf(passiveConfig.getOrDefault("type", "NONE")); }
    public String getActiveType() { return String.valueOf(activeConfig.getOrDefault("type", "NONE")); }
    public String getParticleType() { return String.valueOf(particleConfig.getOrDefault("type", "NONE")); }
    public boolean hasActiveAbility() { return !getActiveType().equals("NONE"); }
    public boolean hasPassiveEffect() { return !getPassiveType().equals("NONE"); }
}
