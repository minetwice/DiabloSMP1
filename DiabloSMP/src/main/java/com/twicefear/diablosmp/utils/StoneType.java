package com.twicefear.diablosmp.utils;

public enum StoneType {
    EARTHQUAKE("Earthquake Stone", "&4&l⬛", "earthquake_stone"),
    INFERNO("Inferno Stone", "&c&l🔥", "inferno_stone"),
    FROSTBITE("Frostbite Stone", "&b&l❄️", "frostbite_stone"),
    TEMPEST("Tempest Stone", "&7&l🌪️", "tempest_stone"),
    VOID("Void Stone", "&5&l🌑", "void_stone"),
    LIGHTNING("Lightning Stone", "&e&l⚡", "lightning_stone"),
    CELESTIAL("Celestial Stone", "&d&l⭐", "celestial_stone"),
    ABYSSAL("Abyssal Stone", "&8&l🕳️", "abyssal_stone"),
    SOLAR("Solar Stone", "&6&l☀️", "solar_stone"),
    LUNAR("Lunar Stone", "&f&l🌙", "lunar_stone"),
    CHRONO("Chrono Stone", "&3&l⏰", "chrono_stone"),
    SPECTRAL("Spectral Stone", "&9&l👻", "spectral_stone"),
    PRIMAL("Primal Stone", "&2&l🌿", "primal_stone"),
    COSMIC("Cosmic Stone", "&5&l🌌", "cosmic_stone"),
    ETHEREAL("Ethereal Stone", "&b&l💎", "ethereal_stone"),
    SHADOW("Shadow Stone", "&7&l🌑", "shadow_stone");
    
    private final String displayName;
    private final String coloredName;
    private final String modelName;
    
    StoneType(String displayName, String coloredName, String modelName) {
        this.displayName = displayName;
        this.coloredName = coloredName;
        this.modelName = modelName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getColoredName() {
        return coloredName;
    }
    
    public String getModelName() {
        return modelName;
    }
}
