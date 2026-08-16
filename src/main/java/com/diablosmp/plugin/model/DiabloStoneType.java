package com.diablosmp.plugin.model;

import org.bukkit.Material;

public enum DiabloStoneType {
    FROST("FROST", "<color:#00FFFF>Diablo Frost</color>", "#00FFFF", Material.PACKED_ICE, 1001, TargetingShape.CONE),
    MIRAGE("MIRAGE", "<color:#00AAAA>Diablo Mirage</color>", "#00AAAA", Material.PHANTOM_MEMBRANE, 1002, TargetingShape.RADIUS),
    QUAKE("QUAKE", "<color:#8B4513>Diablo Quake</color>", "#8B4513", Material.COBBLESTONE, 1003, TargetingShape.RADIUS),
    HELLGATE("HELLGATE", "<color:#FF3300>Diablo Hellgate</color>", "#FF3300", Material.NETHER_BRICK, 1004, TargetingShape.RADIUS),
    VERTIGO("VERTIGO", "<color:#AA00AA>Diablo Vertigo</color>", "#AA00AA", Material.AMETHYST_SHARD, 1005, TargetingShape.RADIUS),
    MARIONETTE("MARIONETTE", "<color:#FF0000>Diablo Marionette</color>", "#FF0000", Material.REDSTONE, 1006, TargetingShape.RADIUS);

    private final String id;
    private final String defaultDisplayName;
    private final String colorHex;
    private final Material fallbackMaterial;
    private final int defaultCustomModelData;
    private final TargetingShape defaultShape;

    DiabloStoneType(String id, String defaultDisplayName, String colorHex, Material fallbackMaterial, int defaultCustomModelData, TargetingShape defaultShape) {
        this.id = id;
        this.defaultDisplayName = defaultDisplayName;
        this.colorHex = colorHex;
        this.fallbackMaterial = fallbackMaterial;
        this.defaultCustomModelData = defaultCustomModelData;
        this.defaultShape = defaultShape;
    }

    public String getId() {
        return id;
    }

    public String getDefaultDisplayName() {
        return defaultDisplayName;
    }

    public String getColorHex() {
        return colorHex;
    }

    public Material getFallbackMaterial() {
        return fallbackMaterial;
    }

    public int getDefaultCustomModelData() {
        return defaultCustomModelData;
    }

    public TargetingShape getDefaultShape() {
        return defaultShape;
    }

    public static DiabloStoneType fromString(String input) {
        if (input == null) return null;
        for (DiabloStoneType type : values()) {
            if (type.name().equalsIgnoreCase(input) || type.id.equalsIgnoreCase(input)) {
                return type;
            }
        }
        return null;
    }
}
