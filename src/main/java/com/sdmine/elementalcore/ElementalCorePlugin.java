package com.sdmine.elementalcore;

import com.sdmine.elementalcore.commands.ElementalCommand;
import com.sdmine.elementalcore.gui.SocketGUIListener;
import com.sdmine.elementalcore.integration.EIHook;
import com.sdmine.elementalcore.items.ItemFactory;
import com.sdmine.elementalcore.listeners.FirstJoinListener;
import com.sdmine.elementalcore.listeners.PassiveEffectTask;
import com.sdmine.elementalcore.listeners.WeaponUseListener;
import com.sdmine.elementalcore.recipes.RecipeLoader;
import com.sdmine.elementalcore.socket.SocketManager;
import com.sdmine.elementalcore.variants.VariantRegistry;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ElementalCorePlugin extends JavaPlugin {

    private static ElementalCorePlugin instance;
    private ItemFactory itemFactory;
    private SocketManager socketManager;
    private VariantRegistry variantRegistry;
    private RecipeLoader recipeLoader;
    private EIHook eiHook;
    private PassiveEffectTask passiveTask;
    private SocketGUIListener socketGUIListener;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        FileConfiguration config = getConfig();

        this.eiHook = new EIHook();
        eiHook.hook();

        this.itemFactory = new ItemFactory(this);
        this.socketManager = new SocketManager(this);
        this.variantRegistry = new VariantRegistry(this);
        this.variantRegistry.loadVariants();

        this.recipeLoader = new RecipeLoader(this);
        recipeLoader.loadAllRecipes();

        Bukkit.getPluginManager().registerEvents(new FirstJoinListener(this), this);
        this.socketGUIListener = new SocketGUIListener(this);
        Bukkit.getPluginManager().registerEvents(socketGUIListener, this);
        Bukkit.getPluginManager().registerEvents(new WeaponUseListener(this), this);

        this.passiveTask = new PassiveEffectTask(this);
        passiveTask.runTaskTimer(this, 20L, 40L);

        ElementalCommand command = new ElementalCommand(this);
        getCommand("elemental").setExecutor(command);
        getCommand("elemental").setTabCompleter(command);

        getLogger().info("Elemental Core Weapon System v" + getPluginMeta().getVersion() + " enabled!");
        getLogger().info("ExecutableItems hooked: " + eiHook.isHooked());
    }

    @Override
    public void onDisable() {
        if (recipeLoader != null) recipeLoader.removeAllRecipes();
        if (passiveTask != null) passiveTask.cancel();
        getLogger().info("Elemental Core Weapon System disabled.");
    }

    public void reloadPlugin() {
        reloadConfig();
        variantRegistry.loadVariants();
        recipeLoader.removeAllRecipes();
        recipeLoader.loadAllRecipes();
        getLogger().info("Configuration reloaded.");
    }

    public static ElementalCorePlugin getInstance() { return instance; }
    public ItemFactory getItemFactory() { return itemFactory; }
    public SocketManager getSocketManager() { return socketManager; }
    public VariantRegistry getVariantRegistry() { return variantRegistry; }
    public RecipeLoader getRecipeLoader() { return recipeLoader; }
    public EIHook getEiHook() { return eiHook; }
    public SocketGUIListener getSocketGUIListener() { return socketGUIListener; }
}
