package com.diablosmp.plugin;

import com.diablosmp.plugin.ability.AbilityManager;
import com.diablosmp.plugin.command.DiabloCommand;
import com.diablosmp.plugin.config.ConfigManager;
import com.diablosmp.plugin.gui.DiabloMenuGUI;
import com.diablosmp.plugin.gui.StarterGUI;
import com.diablosmp.plugin.listener.InteractionListener;
import com.diablosmp.plugin.listener.PlayerConnectionListener;
import com.diablosmp.plugin.listener.SoulboundListener;
import com.diablosmp.plugin.service.*;
import com.diablosmp.plugin.storage.StorageService;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class DiabloSMP extends JavaPlugin {
    private static DiabloSMP instance;
    private ConfigManager configManager;
    private StorageService storageService;
    private CooldownService cooldownService;
    private HudService hudService;
    private TargetingService targetingService;
    private DamageService damageService;
    private VisualAndSoundService visualAndSoundService;
    private AbilityManager abilityManager;
    private CastingService castingService;

    @Override
    public void onEnable() {
        instance = this;
        this.configManager = new ConfigManager(this);
        this.configManager.loadConfigurations();

        this.storageService = new StorageService(this);
        this.storageService.init();

        this.cooldownService = new CooldownService(this);
        this.hudService = new HudService(this);
        this.hudService.start();

        this.targetingService = new TargetingService(this);
        this.damageService = new DamageService(this);
        this.visualAndSoundService = new VisualAndSoundService(this);
        this.abilityManager = new AbilityManager(this);
        this.castingService = new CastingService(this);

        // Register event listeners
        getServer().getPluginManager().registerEvents(new InteractionListener(this), this);
        getServer().getPluginManager().registerEvents(new SoulboundListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(this), this);
        getServer().getPluginManager().registerEvents(new DiabloMenuGUI(), this);
        getServer().getPluginManager().registerEvents(new StarterGUI(), this);

        // Register commands
        DiabloCommand diabloCommand = new DiabloCommand(this);
        PluginCommand cmd = getCommand("diablosmp");
        if (cmd != null) {
            cmd.setExecutor(diabloCommand);
            cmd.setTabCompleter(diabloCommand);
        }

        getLogger().info("DiabloSMP has been enabled successfully!");
    }

    @Override
    public void onDisable() {
        if (abilityManager != null) {
            abilityManager.cleanupAll();
        }
        if (hudService != null) {
            hudService.stop();
        }
        if (storageService != null) {
            storageService.close();
        }
        getLogger().info("DiabloSMP has been disabled.");
    }

    public static DiabloSMP getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public StorageService getStorageService() {
        return storageService;
    }

    public CooldownService getCooldownService() {
        return cooldownService;
    }

    public HudService getHudService() {
        return hudService;
    }

    public TargetingService getTargetingService() {
        return targetingService;
    }

    public DamageService getDamageService() {
        return damageService;
    }

    public VisualAndSoundService getVisualAndSoundService() {
        return visualAndSoundService;
    }

    public AbilityManager getAbilityManager() {
        return abilityManager;
    }

    public CastingService getCastingService() {
        return castingService;
    }
}
