package com.twicefear.diablosmp;

import com.twicefear.diablosmp.commands.DiabloCommand;
import com.twicefear.diablosmp.commands.StoneCommand;
import com.twicefear.diablosmp.listeners.PlayerListener;
import com.twicefear.diablosmp.listeners.StoneInteractionListener;
import com.twicefear.diablosmp.managers.*;
import org.bukkit.plugin.java.JavaPlugin;

public class DiabloSMPPlugin extends JavaPlugin {
    
    private static DiabloSMPPlugin instance;
    
    private StoneManager stoneManager;
    private AbilityManager abilityManager;
    private CooldownManager cooldownManager;
    private SMPManager smpManager;
    private ParticleManager particleManager;
    private UIManager uiManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Save default config
        saveDefaultConfig();
        
        // Initialize managers
        this.stoneManager = new StoneManager(this);
        this.abilityManager = new AbilityManager(this);
        this.cooldownManager = new CooldownManager(this);
        this.smpManager = new SMPManager(this);
        this.particleManager = new ParticleManager(this);
        this.uiManager = new UIManager(this);
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new StoneInteractionListener(this), this);
        
        // Register commands
        getCommand("diablosmp").setExecutor(new DiabloCommand(this));
        getCommand("diablostone").setExecutor(new StoneCommand(this));
        
        // Initialize 15 Diablo Stones
        stoneManager.initializeStones();
        
        getLogger().info("DiabloSMP has been enabled! Author: Twicefear");
        getLogger().info("15 Diablo Stones loaded with complex abilities!");
    }
    
    @Override
    public void onDisable() {
        // Save player data
        if (smpManager != null) {
            smpManager.saveState();
        }
        
        getLogger().info("DiabloSMP has been disabled!");
    }
    
    public static DiabloSMPPlugin getInstance() {
        return instance;
    }
    
    public StoneManager getStoneManager() {
        return stoneManager;
    }
    
    public AbilityManager getAbilityManager() {
        return abilityManager;
    }
    
    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }
    
    public SMPManager getSmpManager() {
        return smpManager;
    }
    
    public ParticleManager getParticleManager() {
        return particleManager;
    }
    
    public UIManager getUiManager() {
        return uiManager;
    }
}
