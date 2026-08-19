package com.twicefear.diablosmp;

import com.twicefear.diablosmp.ability.AbilityManager;
import com.twicefear.diablosmp.command.DSMPCommand;
import com.twicefear.diablosmp.command.DStoneCommand;
import com.twicefear.diablosmp.config.ConfigManager;
import com.twicefear.diablosmp.config.MessageManager;
import com.twicefear.diablosmp.listener.AbsorbListener;
import com.twicefear.diablosmp.listener.AbilityListener;
import com.twicefear.diablosmp.listener.JoinListener;
import com.twicefear.diablosmp.listener.ProtectionListener;
import com.twicefear.diablosmp.listener.SMPListener;
import com.twicefear.diablosmp.manager.AbsorbManager;
import com.twicefear.diablosmp.manager.CooldownManager;
import com.twicefear.diablosmp.manager.PlayerDataManager;
import com.twicefear.diablosmp.manager.SMPManager;
import com.twicefear.diablosmp.manager.StoneManager;
import com.twicefear.diablosmp.task.ActionBarTask;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * DiabloSMP main plugin class.
 *
 * Author: Twicefear
 */
public final class DiabloSMP extends JavaPlugin {

    private static DiabloSMP instance;

    private ConfigManager configManager;
    private MessageManager messageManager;
    private StoneManager stoneManager;
    private PlayerDataManager playerDataManager;
    private CooldownManager cooldownManager;
    private AbsorbManager absorbManager;
    private SMPManager smpManager;
    private AbilityManager abilityManager;
    private ActionBarTask actionBarTask;

    @Override
    public void onEnable() {
        instance = this;

        // Config & messages
        saveDefaultConfig();
        this.configManager = new ConfigManager(this);
        this.configManager.load();
        this.messageManager = new MessageManager(this);
        this.messageManager.load();

        // Managers
        this.stoneManager = new StoneManager(this);
        this.playerDataManager = new PlayerDataManager(this);
        this.cooldownManager = new CooldownManager();
        this.absorbManager = new AbsorbManager(this);
        this.abilityManager = new AbilityManager(this);
        this.smpManager = new SMPManager(this);

        // Listeners
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new AbsorbListener(this), this);
        getServer().getPluginManager().registerEvents(new AbilityListener(this), this);
        getServer().getPluginManager().registerEvents(new ProtectionListener(this), this);
        getServer().getPluginManager().registerEvents(new SMPListener(this), this);

        // Commands
        DSMPCommand dsmpCmd = new DSMPCommand(this);
        getCommand("diablosmp").setExecutor(dsmpCmd);
        getCommand("diablosmp").setTabCompleter(dsmpCmd);
        getServer().getPluginManager().registerEvents(new com.twicefear.diablosmp.listener.StartGUIListener(this, dsmpCmd), this);
        DStoneCommand dStoneCmd = new DStoneCommand(this);
        getCommand("diablostone").setExecutor(dStoneCmd);
        getCommand("diablostone").setTabCompleter(dStoneCmd);

        // Recurring tasks
        this.actionBarTask = new ActionBarTask(this);
        this.actionBarTask.runTaskTimer(this, 20L, configManager.cooldownRefreshTicks());

        getLogger().info("DiabloSMP enabled. Author: Twicefear");
        getLogger().info("15 diablo stones registered. SMP state: " + smpManager.getState());
    }

    @Override
    public void onDisable() {
        if (smpManager != null) {
            smpManager.shutdown();
        }
        if (playerDataManager != null) {
            playerDataManager.saveAll();
        }
        getLogger().info("DiabloSMP disabled.");
    }

    public void reload() {
        reloadConfig();
        configManager.load();
        messageManager.load();
    }

    public static DiabloSMP getInstance() {
        return instance;
    }

    public ConfigManager config() {
        return configManager;
    }

    public MessageManager messages() {
        return messageManager;
    }

    public StoneManager stones() {
        return stoneManager;
    }

    public PlayerDataManager players() {
        return playerDataManager;
    }

    public CooldownManager cooldowns() {
        return cooldownManager;
    }

    public AbsorbManager absorb() {
        return absorbManager;
    }

    public AbilityManager abilities() {
        return abilityManager;
    }

    public SMPManager smp() {
        return smpManager;
    }

    public ActionBarTask actionBar() {
        return actionBarTask;
    }

    public void debug(String message) {
        if (configManager.isDebugEnabled()) {
            getLogger().info("[DEBUG] " + message);
        }
    }
}
