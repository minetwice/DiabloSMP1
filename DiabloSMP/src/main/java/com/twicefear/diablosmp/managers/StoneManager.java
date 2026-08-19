package com.twicefear.diablosmp.managers;

import com.twicefear.diablosmp.DiabloSMPPlugin;
import com.twicefear.diablosmp.utils.DiabloStone;
import com.twicefear.diablosmp.utils.StoneType;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class StoneManager {
    
    private final DiabloSMPPlugin plugin;
    private final Map<UUID, DiabloStone> playerStones = new HashMap<>();
    private final Map<String, DiabloStone> stoneRegistry = new HashMap<>();
    
    public StoneManager(DiabloSMPPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void initializeStones() {
        // Register all 15 Diablo Stones
        registerStone(StoneType.EARTHQUAKE);
        registerStone(StoneType.INFERNO);
        registerStone(StoneType.FROSTBITE);
        registerStone(StoneType.TEMPEST);
        registerStone(StoneType.VOID);
        registerStone(StoneType.LIGHTNING);
        registerStone(StoneType.CELESTIAL);
        registerStone(StoneType.ABYSSAL);
        registerStone(StoneType.SOLAR);
        registerStone(StoneType.LUNAR);
        registerStone(StoneType.CHRONO);
        registerStone(StoneType.SPECTRAL);
        registerStone(StoneType.PRIMAL);
        registerStone(StoneType.COSMIC);
        registerStone(StoneType.ETHEREAL);
        registerStone(StoneType.SHADOW);
        
        plugin.getLogger().info("Registered 15 Diablo Stones!");
    }
    
    private void registerStone(StoneType type) {
        FileConfiguration config = plugin.getConfig();
        int primaryCooldown = config.getInt("abilities." + type.name().toLowerCase() + ".primary-cooldown", 60);
        int secondaryCooldown = config.getInt("abilities." + type.name().toLowerCase() + ".secondary-cooldown", 120);
        
        DiabloStone stone = new DiabloStone(type, primaryCooldown, secondaryCooldown);
        stoneRegistry.put(type.name(), stone);
    }
    
    public DiabloStone getRandomStone() {
        List<DiabloStone> stones = new ArrayList<>(stoneRegistry.values());
        if (stones.isEmpty()) return null;
        Random random = new Random();
        return stones.get(random.nextInt(stones.size()));
    }
    
    public DiabloStone getStoneByType(StoneType type) {
        return stoneRegistry.get(type.name());
    }
    
    public boolean hasStoneAbsorbed(UUID playerUuid) {
        return playerStones.containsKey(playerUuid);
    }
    
    public DiabloStone getPlayerStone(UUID playerUuid) {
        return playerStones.get(playerUuid);
    }
    
    public void absorbStone(UUID playerUuid, DiabloStone stone) {
        playerStones.put(playerUuid, stone);
    }
    
    public void removeStone(UUID playerUuid) {
        playerStones.remove(playerUuid);
    }
    
    public Map<UUID, DiabloStone> getAllPlayerStones() {
        return Collections.unmodifiableMap(playerStones);
    }
    
    public boolean canDropStone() {
        return !plugin.getConfig().getBoolean("stones.prevent-drop", true);
    }
    
    public boolean dropsOnDeath() {
        return plugin.getConfig().getBoolean("stones.drop-on-death", false);
    }
}
