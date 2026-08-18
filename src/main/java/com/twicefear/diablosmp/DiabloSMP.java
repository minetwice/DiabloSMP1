package com.twicefear.diablosmp;

import com.twicefear.diablosmp.ability.AbilityRegistry;
import com.twicefear.diablosmp.api.AethelionHook;
import com.twicefear.diablosmp.command.AbilityCommand;
import com.twicefear.diablosmp.listener.AbilityClickListener;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class DiabloSMP extends JavaPlugin {

    private static DiabloSMP instance;
    private AbilityRegistry abilityRegistry;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        // Hook into Aethelion
        if (!AethelionHook.hook()) {
            getLogger().severe("Aethelion plugin NOT found! DiabloSMP requires Aethelion to function.");
            getLogger().severe("Disabling DiabloSMP.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().info("Successfully hooked into Aethelion!");

        // Initialize ability registry (registers all 15 abilities)
        abilityRegistry = new AbilityRegistry(this);
        abilityRegistry.registerAll();

        // Register command
        AbilityCommand cmd = new AbilityCommand(this);
        getCommand("ability").setExecutor(cmd);
        getCommand("ability").setTabCompleter(cmd);
        getCommand("abilities").setExecutor(cmd);

        // Register listener for right-click trigger
        getServer().getPluginManager().registerEvents(new AbilityClickListener(this), this);

        getLogger().info("DiabloSMP v" + getPluginMeta().getVersion() + " enabled with "
                + abilityRegistry.getAbilityCount() + " abilities.");
    }

    @Override
    public void onDisable() {
        getLogger().info("DiabloSMP disabled.");
    }

    public static DiabloSMP getInstance() {
        return instance;
    }

    public AbilityRegistry getAbilityRegistry() {
        return abilityRegistry;
    }

    public String prefix() {
        return ChatColor.GOLD + "[" + ChatColor.YELLOW + "DiabloSMP" + ChatColor.GOLD + "] " + ChatColor.RESET;
    }
}
