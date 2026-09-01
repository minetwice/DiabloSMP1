package com.sdmine.elementalcore.core;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import java.awt.Color;

public enum CoreType {
    FLAME("Flame", ChatColor.RED, Material.MAGMA_CREAM, new Color(220, 50, 50)),
    WATER("Water", ChatColor.BLUE, Material.HEART_OF_THE_SEA, new Color(50, 100, 220)),
    TEMPEST("Tempest", ChatColor.GREEN, Material.EMERALD, new Color(80, 200, 80)),
    TERRA("Terra", ChatColor.YELLOW, Material.GOLD_NUGGET, new Color(220, 200, 50)),
    VOID("Void", ChatColor.DARK_PURPLE, Material.ENDER_PEARL, new Color(150, 50, 200)),
    LIGHT("Light", ChatColor.WHITE, Material.QUARTZ, new Color(240, 240, 220));

    private final String symbol;
    private final ChatColor chatColor;
    private final Material defaultMaterial;
    private final Color rgbColor;

    CoreType(String symbol, ChatColor chatColor, Material defaultMaterial, Color rgbColor) {
        this.symbol = symbol;
        this.chatColor = chatColor;
        this.defaultMaterial = defaultMaterial;
        this.rgbColor = rgbColor;
    }

    public String getSymbol() { return symbol; }
    public ChatColor getChatColor() { return chatColor; }
    public Material getDefaultMaterial() { return defaultMaterial; }
    public Color getRgbColor() { return rgbColor; }

    public static CoreType fromKey(String key) {
        if (key == null) return null;
        try { return CoreType.valueOf(key.toUpperCase().trim()); }
        catch (IllegalArgumentException e) { return null; }
    }

    public String getColoredSymbol() { return chatColor + symbol; }
}
