package com.diablosmp.plugin.ability.impl;

import com.diablosmp.plugin.DiabloSMP;
import com.diablosmp.plugin.ability.DiabloAbility;
import com.diablosmp.plugin.config.StoneConfig;
import com.diablosmp.plugin.model.DiabloStoneType;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

public class AbyssAbility extends DiabloAbility {

    public AbyssAbility(DiabloSMP plugin) {
        super(plugin, DiabloStoneType.ABYSS);
    }

    @Override
    public boolean cast(Player player) {
        StoneConfig config = plugin.getConfigManager().getStoneConfig(stoneType);
        if (config == null) return false;

        Location center = player.getLocation().add(0, 1.0, 0);
        plugin.getVisualAndSoundService().playSound(center, config.getCastSound(), 1.0f, 0.9f);

        // Water tide ring & Riptide pull
        scheduleRepeatingTask(() -> {
            for (int i = 0; i < 360; i += 20) {
                double rad = Math.toRadians(i);
                Location p = center.clone().add(Math.cos(rad) * 3.5, 0.1, Math.sin(rad) * 3.5);
                p.getWorld().spawnParticle(Particle.SPLASH, p, 5, 0.2, 0.2, 0.2, 0.05);
                p.getWorld().spawnParticle(Particle.BUBBLE, p, 3, 0.1, 0.1, 0.1, 0.02);
                plugin.getVisualAndSoundService().spawnDustParticle(p, Color.fromRGB(0, 0, 170), 1.5f, 3);
            }

            List<LivingEntity> targets = plugin.getTargetingService().getTargets(player, stoneType);
            for (LivingEntity target : targets) {
                Vector pull = center.toVector().subtract(target.getLocation().toVector()).normalize().multiply(0.35);
                pull.setY(0.15);
                target.setVelocity(pull);
            }
            plugin.getVisualAndSoundService().playSound(center, config.getChargeSound(), 0.5f, 0.8f);
        }, 0, 2, 30);

        // Oceanic Crushing Surge at tick 30
        scheduleTask(() -> {
            center.getWorld().spawnParticle(Particle.SPLASH, center, 100, 2.0, 1.0, 2.0, 0.2);
            center.getWorld().spawnParticle(Particle.EXPLOSION, center, 2);
            plugin.getDamageService().dealAoeDamage(player, center, config.getRadius(), config.getDirectDamage(), 1.3);
            plugin.getVisualAndSoundService().playSound(center, config.getImpactSound(), 1.1f, 0.8f);

            cleanup();
        }, 30);

        return true;
    }
}
