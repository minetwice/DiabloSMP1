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

public class ChronoAbility extends DiabloAbility {

    public ChronoAbility(DiabloSMP plugin) {
        super(plugin, DiabloStoneType.CHRONO);
    }

    @Override
    public boolean cast(Player player) {
        StoneConfig config = plugin.getConfigManager().getStoneConfig(stoneType);
        if (config == null) return false;

        Location center = player.getLocation();
        plugin.getVisualAndSoundService().playSound(center, config.getCastSound(), 1.0f, 1.2f);

        // Rotating clock field ring & time slow
        scheduleRepeatingTask(() -> {
            for (int i = 0; i < 12; i++) {
                double angle = (System.currentTimeMillis() / 250.0) + (i * Math.PI / 6.0);
                Location clockLoc = center.clone().add(Math.cos(angle) * 4.0, 0.2, Math.sin(angle) * 4.0);
                plugin.getVisualAndSoundService().spawnDustParticle(clockLoc, Color.fromRGB(170, 204, 0), 1.8f, 4);
                clockLoc.getWorld().spawnParticle(Particle.ENCHANT, clockLoc, 3, 0.1, 0.1, 0.1, 0.1);
            }

            List<LivingEntity> targets = plugin.getTargetingService().getTargets(player, stoneType);
            for (LivingEntity target : targets) {
                target.setVelocity(target.getVelocity().multiply(0.2));
            }
            plugin.getVisualAndSoundService().playSound(center, config.getChargeSound(), 0.5f, 1.4f);
        }, 0, 2, 40);

        // Time Shatter at tick 40
        scheduleTask(() -> {
            center.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, center, 1);
            center.getWorld().spawnParticle(Particle.CRIT, center, 80, 3.0, 1.0, 3.0, 0.2);

            List<LivingEntity> targets = plugin.getTargetingService().getTargets(player, stoneType);
            if (!targets.isEmpty()) {
                for (LivingEntity target : targets) {
                    plugin.getDamageService().dealDamage(player, target, config.getDirectDamage(), 1.4);
                }
            } else {
                plugin.getDamageService().dealAoeDamage(player, center, config.getRadius(), config.getFallbackAoeDamage(), 1.2);
            }

            plugin.getVisualAndSoundService().playSound(center, config.getImpactSound(), 1.2f, 1.5f);
            cleanup();
        }, 40);

        return true;
    }
}
