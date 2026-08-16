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

public class LanceAbility extends DiabloAbility {

    public LanceAbility(DiabloSMP plugin) {
        super(plugin, DiabloStoneType.LANCE);
    }

    @Override
    public boolean cast(Player player) {
        StoneConfig config = plugin.getConfigManager().getStoneConfig(stoneType);
        if (config == null) return false;

        player.swingMainHand();
        Location startLoc = player.getEyeLocation();
        Vector dir = startLoc.getDirection().normalize();

        plugin.getVisualAndSoundService().playSound(startLoc, config.getCastSound(), 1.0f, 1.1f);

        // Charging beam ray line preview
        scheduleRepeatingTask(() -> {
            for (double d = 0; d <= config.getRadius(); d += 0.8) {
                Location p = startLoc.clone().add(dir.clone().multiply(d));
                plugin.getVisualAndSoundService().spawnDustParticle(p, Color.fromRGB(255, 170, 0), 1.2f, 2);
            }
            plugin.getVisualAndSoundService().playSound(startLoc, config.getChargeSound(), 0.5f, 1.3f);
        }, 0, 2, 20);

        // Fire Comet Beam at tick 20
        scheduleTask(() -> {
            for (double d = 0; d <= config.getRadius(); d += 0.4) {
                Location p = startLoc.clone().add(dir.clone().multiply(d));
                p.getWorld().spawnParticle(Particle.FLAME, p, 3, 0.1, 0.1, 0.1, 0.02);
                p.getWorld().spawnParticle(Particle.END_ROD, p, 2, 0.05, 0.05, 0.05, 0.01);
                plugin.getVisualAndSoundService().spawnDustParticle(p, Color.fromRGB(255, 215, 0), 2.0f, 4);
            }

            List<LivingEntity> targets = plugin.getTargetingService().getTargets(player, stoneType);
            if (!targets.isEmpty()) {
                for (LivingEntity target : targets) {
                    Location tLoc = target.getLocation().add(0, 1.0, 0);
                    tLoc.getWorld().spawnParticle(Particle.EXPLOSION, tLoc, 1);
                    plugin.getDamageService().dealDamage(player, target, config.getDirectDamage(), 1.5);
                    plugin.getVisualAndSoundService().playSound(tLoc, config.getImpactSound(), 1.0f, 1.0f);
                }
            } else {
                Location endLoc = startLoc.clone().add(dir.clone().multiply(config.getRadius()));
                endLoc.getWorld().spawnParticle(Particle.EXPLOSION, endLoc, 1);
                plugin.getDamageService().dealAoeDamage(player, endLoc, 3.0, config.getFallbackAoeDamage(), 1.2);
            }

            cleanup();
        }, 20);

        return true;
    }
}
