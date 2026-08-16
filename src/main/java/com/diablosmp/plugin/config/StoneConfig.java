package com.diablosmp.plugin.config;

import com.diablosmp.plugin.model.DiabloStoneType;
import com.diablosmp.plugin.model.TargetingShape;
import org.bukkit.Material;

import java.util.List;

public class StoneConfig {
    private final DiabloStoneType type;
    private final boolean enabled;
    private final String displayName;
    private final List<String> description;
    private final double cooldownSeconds;
    private final double directDamage;
    private final double fallbackAoeDamage;
    private final TargetingShape targetingShape;
    private final double radius;
    private final double angle;
    private final int maxTargets;
    private final int customModelData;
    private final Material material;
    private final String castSound;
    private final String chargeSound;
    private final String impactSound;

    public StoneConfig(DiabloStoneType type, boolean enabled, String displayName, List<String> description,
                       double cooldownSeconds, double directDamage, double fallbackAoeDamage,
                       TargetingShape targetingShape, double radius, double angle, int maxTargets,
                       int customModelData, Material material, String castSound, String chargeSound, String impactSound) {
        this.type = type;
        this.enabled = enabled;
        this.displayName = displayName;
        this.description = description;
        this.cooldownSeconds = cooldownSeconds;
        this.directDamage = directDamage;
        this.fallbackAoeDamage = fallbackAoeDamage;
        this.targetingShape = targetingShape;
        this.radius = radius;
        this.angle = angle;
        this.maxTargets = maxTargets;
        this.customModelData = customModelData;
        this.material = material;
        this.castSound = castSound;
        this.chargeSound = chargeSound;
        this.impactSound = impactSound;
    }

    public DiabloStoneType getType() { return type; }
    public boolean isEnabled() { return enabled; }
    public String getDisplayName() { return displayName; }
    public List<String> getDescription() { return description; }
    public double getCooldownSeconds() { return cooldownSeconds; }
    public double getDirectDamage() { return directDamage; }
    public double getFallbackAoeDamage() { return fallbackAoeDamage; }
    public TargetingShape getTargetingShape() { return targetingShape; }
    public double getRadius() { return radius; }
    public double getAngle() { return angle; }
    public int getMaxTargets() { return maxTargets; }
    public int getCustomModelData() { return customModelData; }
    public Material getMaterial() { return material; }
    public String getCastSound() { return castSound; }
    public String getChargeSound() { return chargeSound; }
    public String getImpactSound() { return impactSound; }
}
