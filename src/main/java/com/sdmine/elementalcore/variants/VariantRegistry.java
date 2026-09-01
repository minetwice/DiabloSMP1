package com.sdmine.elementalcore.variants;

import com.sdmine.elementalcore.ElementalCorePlugin;
import com.sdmine.elementalcore.core.CoreType;
import com.sdmine.elementalcore.socket.SocketManager;
import org.bukkit.configuration.ConfigurationSection;
import java.util.HashMap;
import java.util.Map;

public class VariantRegistry {
    private final ElementalCorePlugin plugin;
    private final Map<String, WeaponVariant> variants;
    private WeaponVariant fallbackVariant;

    public VariantRegistry(ElementalCorePlugin plugin) {
        this.plugin = plugin;
        this.variants = new HashMap<>();
    }

    public void loadVariants() {
        variants.clear();
        fallbackVariant = null;
        ConfigurationSection variantsSection = plugin.getConfig().getConfigurationSection("variants");
        if (variantsSection == null) { plugin.getLogger().warning("No 'variants' section in config.yml!"); return; }

        for (String variantId : variantsSection.getKeys(false)) {
            ConfigurationSection vs = variantsSection.getConfigurationSection(variantId);
            if (vs == null) continue;
            WeaponVariant variant = new WeaponVariant(variantId);
            boolean isFallback = (vs.getConfigurationSection("combination") == null);

            if (!isFallback) {
                ConfigurationSection combo = vs.getConfigurationSection("combination");
                if (combo != null) for (String coreKey : combo.getKeys(false)) {
                    CoreType type = CoreType.fromKey(coreKey);
                    if (type != null) variant.getCombination().put(type, combo.getInt(coreKey));
                }
                variant.setCombinationKey(SocketManager.generateCombinationKey(
                    variant.getCombination().entrySet().stream()
                        .flatMap(e -> { CoreType[] arr = new CoreType[e.getValue()]; java.util.Arrays.fill(arr, e.getKey()); return java.util.Arrays.stream(arr); })
                        .toArray(CoreType[]::new)));
            }

            variant.setDisplayName(vs.getString("display_name", "&7Unknown Variant"));
            variant.setCustomModelData(vs.getInt("custom_model_data", 30000));

            ConfigurationSection passive = vs.getConfigurationSection("passive");
            if (passive != null) variant.setPassiveConfig(convertToMap(passive));
            ConfigurationSection active = vs.getConfigurationSection("active");
            if (active != null) { variant.setActiveConfig(convertToMap(active)); variant.setCooldown(active.getInt("cooldown", 0)); }
            ConfigurationSection particles = vs.getConfigurationSection("particles");
            if (particles != null) variant.setParticleConfig(convertToMap(particles));
            variant.setSound(vs.getString("sound", ""));
            variant.setPassiveDescription(genPassiveDesc(variant));
            variant.setActiveDescription(genActiveDesc(variant));

            if (isFallback) fallbackVariant = variant;
            else variants.put(variant.getCombinationKey(), variant);
            plugin.getLogger().info("Loaded variant: " + variantId + (isFallback ? " (fallback)" : " [" + variant.getCombinationKey() + "]"));
        }
    }

    public WeaponVariant matchVariant(CoreType[] sockets) {
        if (sockets == null) return fallbackVariant;
        if (SocketManager.countFilled(sockets) == 0) return null;
        for (WeaponVariant variant : variants.values()) if (variant.matches(sockets)) return variant;
        return fallbackVariant;
    }

    public WeaponVariant getVariant(String key) { return variants.get(key); }
    public WeaponVariant getFallbackVariant() { return fallbackVariant; }
    public Map<String, WeaponVariant> getAllVariants() { return variants; }

    private Map<String, Object> convertToMap(ConfigurationSection section) {
        Map<String, Object> map = new HashMap<>();
        for (String key : section.getKeys(false)) map.put(key, section.get(key));
        return map;
    }

    private String genPassiveDesc(WeaponVariant v) {
        String t = v.getPassiveType(); Map<String, Object> c = v.getPassiveConfig();
        switch (t) {
            case "FIRE_ASPECT": return "Fire Aspect II, burns for " + c.getOrDefault("burn_duration", 6) + "s";
            case "STEAM_CLOUD": return "Blindness II + Slowness II for " + c.getOrDefault("duration", 4) + "s";
            case "ANTI_AQUATIC": return "+50% dmg to water mobs, Wither I for " + c.getOrDefault("wither_duration", 3) + "s";
            case "WATER_BREATHING_DOLPHIN": return "Water Breathing + Dolphin's Grace while held";
            case "ATTACK_SPEED_BURN": return "+25% attack speed, burn on hit";
            case "FROST_SLOW": return "Attacks slow targets by 40%";
            case "RESISTANCE_KNOCKBACK": return "Resistance II + full Knockback Resistance";
            case "LAVA_CRIT": return "30% crit chance spawns lava";
            case "LIFE_STEAL": return "Life Steal: " + (int)(((Number)c.getOrDefault("life_steal_percent",0.12)).doubleValue()*100) + "%";
            case "SPEED_JUMP": return "Speed III + Jump Boost II";
            case "REGEN_LIGHT": return "Regeneration I + emits light";
            case "TRI_BALANCE": return "+15% dmg, +15% speed, Fire Immunity";
            default: return "";
        }
    }

    private String genActiveDesc(WeaponVariant v) {
        String t = v.getActiveType(); Map<String, Object> c = v.getActiveConfig();
        switch (t) {
            case "IGNITION_BURST": return "Ignition Burst (CD: " + c.getOrDefault("cooldown",15) + "s)";
            case "SCALDING_ERUPTION": return "Scalding Eruption (CD: " + c.getOrDefault("cooldown",18) + "s)";
            case "GEYSER_LAUNCH": return "Geyser Launch (CD: " + c.getOrDefault("cooldown",12) + "s)";
            case "TSUNAMI_WAVE": return "Tsunami Wave (CD: " + c.getOrDefault("cooldown",10) + "s)";
            case "FIRE_TORNADO": return "Fire Tornado (CD: " + c.getOrDefault("cooldown",20) + "s)";
            case "ABSOLUTE_ZERO": return "Absolute Zero (CD: " + c.getOrDefault("cooldown",22) + "s)";
            case "EARTHQUAKE_BARRIER": return "Earthquake Barrier (CD: " + c.getOrDefault("cooldown",30) + "s)";
            case "VOLCANIC_PILLAR": return "Volcanic Pillar (CD: " + c.getOrDefault("cooldown",16) + "s)";
            case "SHADOW_STEP_STRIKE": return "Shadow Step (CD: " + c.getOrDefault("cooldown",14) + "s)";
            case "AIR_CUT_DASH": return "Air Cut Dash (CD: " + c.getOrDefault("cooldown",8) + "s)";
            case "HOLY_BEAM": return "Holy Beam (CD: " + c.getOrDefault("cooldown",20) + "s)";
            case "ELEMENTAL_OVERLOAD": return "Elemental Overload (CD: " + c.getOrDefault("cooldown",25) + "s)";
            default: return "";
        }
    }
}
