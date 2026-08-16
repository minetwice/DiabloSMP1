package com.diablosmp.plugin.service;

import com.diablosmp.plugin.DiabloSMP;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class DamageService {
    private final DiabloSMP plugin;

    public DamageService(DiabloSMP plugin) {
        this.plugin = plugin;
    }

    public void dealDamage(Player caster, LivingEntity target, double damage, double knockbackMultiplier) {
        if (target == null || target.isDead() || !target.isValid()) return;
        if (target.equals(caster) && !plugin.getConfig().getBoolean("damage.self-damage", false)) return;

        double kbMult = plugin.getConfig().getDouble("damage.knockback-multiplier", 1.0) * knockbackMultiplier;

        target.damage(damage, caster);

        if (kbMult > 0) {
            Vector dir = target.getLocation().toVector().subtract(caster.getLocation().toVector()).normalize();
            dir.setY(0.35);
            dir.multiply(0.8 * kbMult);
            target.setVelocity(dir);
        }

        int invulTicks = plugin.getConfig().getInt("damage.invulnerability-ticks", 10);
        target.setNoDamageTicks(invulTicks);
    }

    public void dealAoeDamage(Player caster, Location origin, double radius, double damage, double knockbackMultiplier) {
        for (Entity entity : origin.getWorld().getNearbyEntities(origin, radius, radius, radius)) {
            if (entity instanceof LivingEntity living && !entity.equals(caster)) {
                dealDamage(caster, living, damage, knockbackMultiplier);
            }
        }
    }
}
