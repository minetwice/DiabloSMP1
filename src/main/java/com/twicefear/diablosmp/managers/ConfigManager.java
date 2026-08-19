package com.twicefear.diablosmp.managers;

import com.twicefear.diablosmp.DiabloSMP;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigManager {

    private final DiabloSMP plugin;
    private FileConfiguration config;

    public ConfigManager(DiabloSMP plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public String getSmpName() {
        return config.getString("smp.name", "DiabloSmp");
    }

    public void setSmpName(String name) {
        config.set("smp.name", name);
        plugin.saveConfig();
    }

    public boolean isSmpStarted() {
        return config.getBoolean("smp.started", false);
    }

    public void setSmpStarted(boolean started) {
        config.set("smp.started", started);
        plugin.saveConfig();
    }

    public int getGracePeriodDefault() {
        return config.getInt("smp.grace-period-default", 300);
    }

    public int getWorldBorderStartSize() {
        return config.getInt("smp.world-border.start-size", 20);
    }

    public int getWorldBorderFinalSize() {
        return config.getInt("smp.world-border.final-size", 20000);
    }

    public int getWorldBorderExpandDuration() {
        return config.getInt("smp.world-border.expand-duration", 60);
    }

    public boolean isFirstJoinEnabled() {
        return config.getBoolean("first-join.enabled", true);
    }

    public int getFirstJoinAnimationDuration() {
        return config.getInt("first-join.animation-duration", 8);
    }

    public boolean isProtectionDuringAnimation() {
        return config.getBoolean("first-join.protection-during-animation", true);
    }

    public int getShiftsRequired() {
        return config.getInt("absorb.shifts-required", 3);
    }

    public boolean canStonesDropOnDeath() {
        return config.getBoolean("protect.stones-can-drop-on-death", false);
    }

    public boolean canStonesBeStored() {
        return config.getBoolean("protect.stones-can-be-stored", true);
    }

    public boolean canStonesBeDropped() {
        return config.getBoolean("protect.stones-can-be-dropped", false);
    }

    public List<String> getPopupLines() {
        return config.getStringList("popups.lines");
    }

    public String getPrefix() {
        return color(config.getString("messages.prefix", "&8[&cDiablo&4SMP&8] &r"));
    }

    public String getMessage(String path) {
        return color(config.getString("messages." + path, "&cMessage not found"));
    }

    private String color(String text) {
        return text == null ? "" : text.replace('&', '§');
    }
}
