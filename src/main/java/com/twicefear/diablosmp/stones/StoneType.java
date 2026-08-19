package com.twicefear.diablosmp.stones;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;

public enum StoneType {

    INFERNAL_CORE("Infernal Core", ChatColor.RED, Material.NETHER_STAR, Particle.FLAME, 45, 90),
    ABYSSAL_SHARD("Abyssal Shard", ChatColor.DARK_AQUA, Material.PRISMARINE_SHARD, Particle.SOUL, 40, 85),
    EARTHQUAKE_RELIC("Earthquake Relic", ChatColor.GOLD, Material.RAW_GOLD, Particle.BLOCK, 50, 100),
    TEMPEST_ORB("Tempest Orb", ChatColor.AQUA, Material.HEART_OF_THE_SEA, Particle.CLOUD, 35, 80),
    SHADOW_FANG("Shadow Fang", ChatColor.DARK_GRAY, Material.ECHO_SHARD, Particle.SMOKE, 30, 70),
    RADIANT_PRISM("Radiant Prism", ChatColor.YELLOW, Material.AMETHYST_SHARD, Particle.END_ROD, 40, 90),
    FROSTBITE_CRYSTAL("Frostbite Crystal", ChatColor.BLUE, Material.BLUE_ICE, Particle.SNOWFLAKE, 45, 85),
    THUNDERBOLT_CORE("Thunderbolt Core", ChatColor.LIGHT_PURPLE, Material.LIGHTNING_ROD, Particle.ELECTRIC_SPARK, 35, 75),
    BLOODMOON_GEM("Bloodmoon Gem", ChatColor.DARK_RED, Material.REDSTONE, Particle.DAMAGE_INDICATOR, 50, 95),
    NATURES_WRATH("Nature's Wrath", ChatColor.GREEN, Material.MOSS_BLOCK, Particle.HAPPY_VILLAGER, 40, 80),
    PHANTOM_ECHO("Phantom Echo", ChatColor.GRAY, Material.PHANTOM_MEMBRANE, Particle.SOUL_FIRE_FLAME, 25, 60),
    CHAOS_FRAGMENT("Chaos Fragment", ChatColor.DARK_PURPLE, Material.END_CRYSTAL, Particle.PORTAL, 55, 110),
    DRAGONHEART_SCALE("Dragonheart Scale", ChatColor.DARK_GREEN, Material.DRAGON_BREATH, Particle.DRAGON_BREATH, 60, 120),
    VOIDWALKER_STONE("Voidwalker Stone", ChatColor.BLACK, Material.ENDER_EYE, Particle.REVERSE_PORTAL, 45, 90),
    CELESTIAL_STAR("Celestial Star", ChatColor.WHITE, Material.NETHER_STAR, Particle.FIREWORK, 50, 100);

    private final String displayName;
    private final ChatColor color;
    private final Material material;
    private final Particle particle;
    private final int primaryCooldown;   // seconds
    private final int secondaryCooldown; // seconds

    StoneType(String displayName, ChatColor color, Material material, Particle particle, int primaryCooldown, int secondaryCooldown) {
        this.displayName = displayName;
        this.color = color;
        this.material = material;
        this.particle = particle;
        this.primaryCooldown = primaryCooldown;
        this.secondaryCooldown = secondaryCooldown;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ChatColor getColor() {
        return color;
    }

    public Material getMaterial() {
        return material;
    }

    public Particle getParticle() {
        return particle;
    }

    public int getPrimaryCooldown() {
        return primaryCooldown;
    }

    public int getSecondaryCooldown() {
        return secondaryCooldown;
    }

    public String getColoredName() {
        return color + "§l" + displayName;
    }
}
