package me.twicefear.diablosmp;

import me.twicefear.diablosmp.stone.StoneItemManager;
import me.twicefear.diablosmp.user.UserManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class DiabloSMP extends JavaPlugin {

    private static DiabloSMP instance;
    private StoneItemManager stoneItemManager;
    private UserManager userManager;
    private me.twicefear.diablosmp.smp.SMPManager smpManager;
    private me.twicefear.diablosmp.smp.StartGUIManager startGuiManager;
    private me.twicefear.diablosmp.hud.HUDManager hudManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        this.stoneItemManager = new StoneItemManager(this);
        this.userManager = new UserManager(this);
        this.smpManager = new me.twicefear.diablosmp.smp.SMPManager(this);
        this.startGuiManager = new me.twicefear.diablosmp.smp.StartGUIManager(this, smpManager);
        this.hudManager = new me.twicefear.diablosmp.hud.HUDManager(this);

        // Register Listeners
        getServer().getPluginManager().registerEvents(new me.twicefear.diablosmp.listener.FirstJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new me.twicefear.diablosmp.listener.AbsorptionListener(this), this);
        getServer().getPluginManager().registerEvents(new me.twicefear.diablosmp.listener.ProtectionListener(this), this);
        getServer().getPluginManager().registerEvents(startGuiManager, this);
        getServer().getPluginManager().registerEvents(new me.twicefear.diablosmp.ability.AbilityListener(this), this);

        // Register Commands
        me.twicefear.diablosmp.command.DiabloCommand diabloCmd = new me.twicefear.diablosmp.command.DiabloCommand(this, startGuiManager);
        getCommand("diablosmp").setExecutor(diabloCmd);
        getCommand("diablosmp").setTabCompleter(diabloCmd);

        getLogger().info("DiabloSMP Plugin loaded successfully! Author: Twicefear");
    }

    public me.twicefear.diablosmp.smp.SMPManager getSmpManager() {
        return smpManager;
    }

    @Override
    public void onDisable() {
        if (userManager != null) {
            for (Player player : getServer().getOnlinePlayers()) {
                userManager.savePlayerData(player);
            }
        }
        getLogger().info("DiabloSMP Plugin disabled.");
    }

    public static DiabloSMP getInstance() {
        return instance;
    }

    public StoneItemManager getStoneItemManager() {
        return stoneItemManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }
}
