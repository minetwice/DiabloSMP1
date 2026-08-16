package com.diablosmp.plugin.config;

import com.diablosmp.plugin.DiabloSMP;
import com.diablosmp.plugin.model.DiabloStoneType;
import com.diablosmp.plugin.model.TargetingShape;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigManager {
    private final DiabloSMP plugin;
    private FileConfiguration config;
    private FileConfiguration messagesConfig;
    private File messagesFile;

    private final Map<DiabloStoneType, StoneConfig> stoneConfigs = new EnumMap<>(DiabloStoneType.class);

    public ConfigManager(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    public void loadConfigurations() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();

        this.messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        this.messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        loadStoneConfigs();
    }

    private void loadStoneConfigs() {
        stoneConfigs.clear();
        ConfigurationSection section = config.getConfigurationSection("stones.types");

        for (DiabloStoneType type : DiabloStoneType.values()) {
            ConfigurationSection stoneSec = section != null ? section.getConfigurationSection(type.name()) : null;

            boolean enabled = stoneSec == null || stoneSec.getBoolean("enabled", true);
            String displayName = stoneSec != null ? stoneSec.getString("display-name", type.getDefaultDisplayName()) : type.getDefaultDisplayName();
            List<String> description = stoneSec != null ? stoneSec.getStringList("description") : Collections.singletonList("Diablo ability stone");
            double cooldown = stoneSec != null ? stoneSec.getDouble("cooldown-seconds", 20.0) : 20.0;
            double directDamage = stoneSec != null ? stoneSec.getDouble("damage.direct", 10.0) : 10.0;
            double fallbackAoe = stoneSec != null ? stoneSec.getDouble("damage.fallback-aoe", 5.0) : 5.0;

            String shapeStr = stoneSec != null ? stoneSec.getString("targeting.shape", type.getDefaultShape().name()) : type.getDefaultShape().name();
            TargetingShape shape;
            try {
                shape = TargetingShape.valueOf(shapeStr.toUpperCase());
            } catch (Exception e) {
                shape = type.getDefaultShape();
            }

            double radius = stoneSec != null ? stoneSec.getDouble("targeting.radius", 10.0) : 10.0;
            double angle = stoneSec != null ? stoneSec.getDouble("targeting.angle", 90.0) : 90.0;
            int maxTargets = stoneSec != null ? stoneSec.getInt("targeting.max-targets", 10) : 10;
            int customModelData = stoneSec != null ? stoneSec.getInt("custom-model-data", type.getDefaultCustomModelData()) : type.getDefaultCustomModelData();

            String matStr = stoneSec != null ? stoneSec.getString("material", type.getFallbackMaterial().name()) : type.getFallbackMaterial().name();
            Material material = Material.matchMaterial(matStr);
            if (material == null) material = type.getFallbackMaterial();

            String castSound = stoneSec != null ? stoneSec.getString("sounds.cast", "ENTITY_ENDER_DRAGON_FLAP") : "ENTITY_ENDER_DRAGON_FLAP";
            String chargeSound = stoneSec != null ? stoneSec.getString("sounds.charge", "BLOCK_AMETHYST_CHIME") : "BLOCK_AMETHYST_CHIME";
            String impactSound = stoneSec != null ? stoneSec.getString("sounds.impact", "ENTITY_GENERIC_EXPLODE") : "ENTITY_GENERIC_EXPLODE";

            StoneConfig stoneConfig = new StoneConfig(
                    type, enabled, displayName, description, cooldown, directDamage, fallbackAoe,
                    shape, radius, angle, maxTargets, customModelData, material, castSound, chargeSound, impactSound
            );
            stoneConfigs.put(type, stoneConfig);
        }
    }

    public FileConfiguration getConfig() { return config; }
    public FileConfiguration getMessagesConfig() { return messagesConfig; }

    public StoneConfig getStoneConfig(DiabloStoneType type) {
        return stoneConfigs.get(type);
    }

    public Map<DiabloStoneType, StoneConfig> getStoneConfigs() {
        return Collections.unmodifiableMap(stoneConfigs);
    }

    public String getMessage(String path) {
        return messagesConfig.getString(path, "<red>Missing message: " + path + "</red>");
    }

    public String getPrefix() {
        return messagesConfig.getString("prefix", config.getString("plugin.prefix", "<gradient:#FF5555:#AA0000>DiabloSMP</gradient> <gray>»</gray> "));
    }
}
