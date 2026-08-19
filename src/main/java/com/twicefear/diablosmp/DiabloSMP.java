package com.twicefear.diablosmp;

import com.twicefear.diablosmp.commands.DiabloCommand;
import com.twicefear.diablosmp.listeners.*;
import com.twicefear.diablosmp.managers.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class DiabloSMP extends JavaPlugin {

    private static DiabloSMP instance;
    private ConfigManager configManager;
    private StoneManager stoneManager;
    private AbilityManager abilityManager;
    private CooldownManager cooldownManager;
    private AbsorbManager absorbManager;
    private SMPManager smpManager;
    private FirstJoinManager firstJoinManager;
    private GUIListener guiListener;

    @Override
    public void onEnable() {
        instance = this;

        // Save default config
        saveDefaultConfig();

        // Initialize managers
        this.configManager = new ConfigManager(this);
        this.stoneManager = new StoneManager(this);
        this.abilityManager = new AbilityManager(this);
        this.cooldownManager = new CooldownManager(this);
        this.absorbManager = new AbsorbManager(this);
        this.smpManager = new SMPManager(this);
        this.firstJoinManager = new FirstJoinManager(this);
        this.guiListener = new GUIListener(this);

        // Register commands
        getCommand("diablosmp").setExecutor(new DiabloCommand(this));
        getCommand("diablosmp").setTabCompleter(new DiabloCommand(this));

        // Register listeners
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new AbilityListener(this), this);
        Bukkit.getPluginManager().registerEvents(new AbsorbListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ProtectionListener(this), this);
        Bukkit.getPluginManager().registerEvents(new InventoryListener(this), this);
        Bukkit.getPluginManager().registerEvents(guiListener, this);

        // Load stones
        stoneManager.loadStones();

        getLogger().info("========================================");
        getLogger().info("  DiabloSMP v" + getDescription().getVersion() + " enabled!");
        getLogger().info("  Author: Twicefear");
        getLogger().info("  15 Legendary Diablo Stones loaded");
        getLogger().info("========================================");
    }

    @Override
    public void onDisable() {
        if (smpManager != null) {
            smpManager.shutdown();
        }
        getLogger().info("DiabloSMP disabled.");
    }

    public static DiabloSMP getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
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

    public AbsorbManager getAbsorbManager() {
        return absorbManager;
    }

    public SMPManager getSmpManager() {
        return smpManager;
    }

    public FirstJoinManager getFirstJoinManager() {
        return firstJoinManager;
    }

    public GUIListener getGuiListener() {
        return guiListener;
    }
}
