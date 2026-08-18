package com.twicefear.diablosmp.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Provides access to the Aethelion plugin's EmoteAPI.
 * Casts the Aethelion plugin instance to EmoteAPI at runtime.
 */
public final class AethelionHook {

    private static EmoteAPI api;

    private AethelionHook() {}

    public static boolean hook() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Aethelion");
        if (plugin instanceof EmoteAPI emoteAPI) {
            api = emoteAPI;
            return true;
        }
        return false;
    }

    public static EmoteAPI get() {
        if (api == null)
            throw new IllegalStateException("Aethelion API not hooked. Call AethelionHook.hook() first.");
        return api;
    }

    public static boolean isAvailable() {
        return api != null;
    }
}
