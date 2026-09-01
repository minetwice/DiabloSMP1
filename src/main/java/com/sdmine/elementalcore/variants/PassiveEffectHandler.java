package com.sdmine.elementalcore.variants;

import com.sdmine.elementalcore.ElementalCorePlugin;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.Map;

public class PassiveEffectHandler {
    private final ElementalCorePlugin plugin;

    public PassiveEffectHandler(ElementalCorePlugin plugin) { this.plugin = plugin; }

    public void applyOnHitPassive(WeaponVariant variant, Player attacker, LivingEntity target, double baseDamage) {
        if (variant == null || !variant.hasPassiveEffect()) return;
        String type = variant.getPassiveType();
        Map<String, Object> cfg = variant.getPassiveConfig();
        switch (type) {
            case "FIRE_ASPECT" -> { int d = toInt(cfg.getOrDefault("burn_duration", 6)); target.setFireTicks(Math.max(target.getFireTicks(), d * 20)); }
            case "STEAM_CLOUD" -> { int dur = toInt(cfg.getOrDefault("duration", 4)) * 20; target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, dur, toInt(cfg.getOrDefault("blindness_level",2))-1)); target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, dur, toInt(cfg.getOrDefault("slowness_level",2))-1)); }
            case "ANTI_AQUATIC" -> { String tn = target.getType().name(); if (tn.equals("DROWNED")||tn.equals("GUARDIAN")||tn.equals("ELDER_GUARDIAN")||tn.equals("SQUID")||tn.equals("DOLPHIN")) { double m = toDouble(cfg.getOrDefault("damage_bonus_multiplier",1.5)); target.damage(baseDamage*(m-1.0)); } target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, toInt(cfg.getOrDefault("wither_duration",3))*20, toInt(cfg.getOrDefault("wither_level",1))-1)); }
            case "ATTACK_SPEED_BURN" -> target.setFireTicks(Math.max(target.getFireTicks(), toInt(cfg.getOrDefault("burn_ticks",20))));
            case "FROST_SLOW" -> target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, toInt(cfg.getOrDefault("slow_level",2))-1));
            case "LAVA_CRIT" -> { if (Math.random() < toDouble(cfg.getOrDefault("crit_chance",0.30))) plugin.getLogger().fine("Lava crit for: " + target.getName()); }
            case "LIFE_STEAL" -> { double pct = toDouble(cfg.getOrDefault("life_steal_percent",0.12)); double heal = baseDamage * pct; double max = attacker.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(); attacker.setHealth(Math.min(attacker.getHealth()+heal, max)); }
            case "TRI_BALANCE" -> { target.damage(baseDamage * toDouble(cfg.getOrDefault("damage_bonus",0.15))); if (toBool(cfg.getOrDefault("fire_immune",true))) attacker.setFireTicks(0); }
            default -> {}
        }
    }

    private int toInt(Object o) { return (o instanceof Number) ? ((Number)o).intValue() : 0; }
    private double toDouble(Object o) { return (o instanceof Number) ? ((Number)o).doubleValue() : 0; }
    private boolean toBool(Object o) { return (o instanceof Boolean) && (Boolean) o; }
}
