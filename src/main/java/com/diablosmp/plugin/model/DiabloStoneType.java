package com.diablosmp.plugin.model;

import org.bukkit.Material;

public enum DiabloStoneType {
    SHARD("SHARD", "<color:#55FFFF>Diablo Shard</color>", "#55FFFF", Material.AMETHYST_SHARD, 1001, TargetingShape.CONE),
    EMBER("EMBER", "<color:#FF3300>Diablo Ember</color>", "#FF3300", Material.FIRE_CHARGE, 1002, TargetingShape.RADIUS),
    HALO("HALO", "<color:#FFD700>Diablo Halo</color>", "#FFD700", Material.NETHER_STAR, 1003, TargetingShape.RADIUS),
    ROOT("ROOT", "<color:#55FF55>Diablo Root</color>", "#55FF55", Material.VINE, 1004, TargetingShape.RADIUS),
    VOID("VOID", "<color:#AA00AA>Diablo Void</color>", "#AA00AA", Material.ECHO_SHARD, 1005, TargetingShape.RADIUS),
    FROST("FROST", "<color:#00FFFF>Diablo Frost</color>", "#00FFFF", Material.PACKED_ICE, 1006, TargetingShape.RADIUS),
    STORM("STORM", "<color:#FFFF55>Diablo Storm</color>", "#FFFF55", Material.LIGHTNING_ROD, 1007, TargetingShape.RADIUS),
    BLOOD("BLOOD", "<color:#FF0000>Diablo Blood</color>", "#FF0000", Material.REDSTONE, 1008, TargetingShape.RADIUS),
    SERAPH("SERAPH", "<color:#FFAA00>Diablo Seraph</color>", "#FFAA00", Material.FEATHER, 1009, TargetingShape.RADIUS),
    GRAVE("GRAVE", "<color:#550055>Diablo Grave</color>", "#550055", Material.OBSIDIAN, 1010, TargetingShape.RADIUS),
    MIRAGE("MIRAGE", "<color:#00AAAA>Diablo Mirage</color>", "#00AAAA", Material.PHANTOM_MEMBRANE, 1011, TargetingShape.RADIUS),
    LANCE("LANCE", "<color:#FFAA00>Diablo Lance</color>", "#FFAA00", Material.BLAZE_ROD, 1012, TargetingShape.LINE),
    ABYSS("ABYSS", "<color:#0000AA>Diablo Abyss</color>", "#0000AA", Material.PRISMARINE_SHARD, 1013, TargetingShape.RADIUS),
    CHRONO("CHRONO", "<color:#AACC00>Diablo Chrono</color>", "#AACC00", Material.CLOCK, 1014, TargetingShape.RADIUS),
    OMEGA("OMEGA", "<gradient:#FF0000:#FFFFFF:#000000>Diablo Omega</gradient>", "#FF0000", Material.DRAGON_EGG, 1015, TargetingShape.RADIUS);

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
